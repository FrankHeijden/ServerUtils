package net.frankheijden.serverutils.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.*;

public class ReflectionUtils {

    public static String NMS;
    public static int MAJOR;
    public static int MINOR;
    public static int PATCH;
    static {
        String bukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
        NMS = bukkitPackage.substring(bukkitPackage.lastIndexOf('.') + 1);

        String[] split = NMS.split("_");
        MAJOR = Integer.parseInt(split[0].substring(1));
        MINOR = Integer.parseInt(split[1]);
        PATCH = Integer.parseInt(split[2].substring(1, 2));
    }

    public static Field getDeclaredField(Class<?> clazz, String field) throws NoSuchFieldException {
        Field f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return f;
    }

    public static Field getField(Class<?> clazz, String field) throws NoSuchFieldException {
        Field f = clazz.getField(field);
        f.setAccessible(true);
        return f;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String method, Class<?>... params) throws NoSuchMethodException {
        Method m = clazz.getDeclaredMethod(method, params);
        m.setAccessible(true);
        return m;
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>... params) throws NoSuchMethodException {
        Method m = clazz.getMethod(method, params);
        m.setAccessible(true);
        return m;
    }

    public static Map<String, Field> getAllFields(Class<?> clazz, FieldParam... fields) {
        Map<String, Field> map = new HashMap<>();
        for (FieldParam fieldParam : fields) {
            if (!fieldParam.versionParam.isCompatible()) continue;
            try {
                map.put(fieldParam.field, getDeclaredField(clazz, fieldParam.field));
            } catch (NoSuchFieldException ignored) {
                try {
                    map.put(fieldParam.field, getField(clazz, fieldParam.field));
                } catch (NoSuchFieldException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return map;
    }

    public static Map<String, Method> getAllMethods(Class<?> clazz, MethodParam... methodParams) {
        Map<String, Method> map = new HashMap<>();
        for (MethodParam methodParam : methodParams) {
            if (!methodParam.versionParam.isCompatible()) continue;
            try {
                map.put(methodParam.method, getDeclaredMethod(clazz, methodParam.method, methodParam.params));
            } catch (NoSuchMethodException ignored) {
                try {
                    map.put(methodParam.method, getMethod(clazz, methodParam.method, methodParam.params));
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return map;
    }

    public static Object invoke(Map<String, Method> map, Object instance, String methodName, Object... params) throws InvocationTargetException, IllegalAccessException {
        Method method = map.get(methodName);
        if (method == null) return null;
        return method.invoke(instance, params);
    }

    public static Object get(Map<String, Field> map, Object instance, String fieldName) throws IllegalAccessException {
        Field field = map.get(fieldName);
        if (field == null) return null;
        return field.get(instance);
    }

    public static void set(Map<String, Field> map, Object instance, String fieldName, Object value) throws IllegalAccessException {
        Field field = map.get(fieldName);
        if (field == null) return;
        field.set(instance, value);
    }

    public static class VersionParam {
        public static VersionParam ALL_VERSIONS = new VersionParam(Integer.MIN_VALUE, Integer.MAX_VALUE);

        public int min;
        public int max;

        private VersionParam(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public static VersionParam versionOf(int ver) {
            return new VersionParam(ver, ver);
        }

        public static VersionParam between(int min, int max) {
            return new VersionParam(min, max);
        }

        public static VersionParam min(int min) {
            return between(min, Integer.MAX_VALUE);
        }

        public static VersionParam max(int max) {
            return between(Integer.MIN_VALUE, max);
        }

        public boolean isCompatible() {
            return VersionParam.isCompatible(this);
        }

        public static boolean isCompatible(VersionParam versionParam) {
            return versionParam.min <= MINOR && MINOR <= versionParam.max;
        }
    }

    public static class FieldParam {
        public String field;
        public VersionParam versionParam;

        private FieldParam(String field, VersionParam versionParam) {
            this.field = field;
            this.versionParam = versionParam;
        }

        public static FieldParam fieldOf(String field, VersionParam versionParam) {
            return new FieldParam(field, versionParam);
        }
    }

    public static class MethodParam {
        public String method;
        public VersionParam versionParam;
        public Class<?>[] params;

        private MethodParam(String method, VersionParam versionParam, Class<?>... params) {
            this.method = method;
            this.versionParam = versionParam;
            this.params = params;
        }

        public static MethodParam methodOf(String method, VersionParam versionParam, Class<?>... params) {
            return new MethodParam(method, versionParam, params);
        }
    }
}
