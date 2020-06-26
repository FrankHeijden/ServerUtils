package net.frankheijden.serverutils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

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

    /**
     * Retrieves a declared field from a class and makes it accessible.
     * @param clazz The class of the method.
     * @param field The field name.
     * @return The specified field.
     * @throws NoSuchFieldException iff field doesn't exist.
     */
    public static Field getDeclaredField(Class<?> clazz, String field) throws NoSuchFieldException {
        Field f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        return f;
    }

    /**
     * Retrieves a field from a class and makes it accessible.
     * @param clazz The class of the method.
     * @param field The field name.
     * @return The specified field.
     * @throws NoSuchFieldException iff field doesn't exist.
     */
    public static Field getField(Class<?> clazz, String field) throws NoSuchFieldException {
        Field f = clazz.getField(field);
        f.setAccessible(true);
        return f;
    }

    /**
     * Retrieves a declared method from a class and makes it accessible.
     * @param clazz The class of the method.
     * @param method The method name.
     * @param params The parameters of the method.
     * @return The specified method.
     * @throws NoSuchMethodException iff method doesn't exist.
     */
    public static Method getDeclaredMethod(Class<?> clazz, String method, Class<?>... params)
            throws NoSuchMethodException {
        Method m = clazz.getDeclaredMethod(method, params);
        m.setAccessible(true);
        return m;
    }

    /**
     * Retrieves a method from a class and makes it accessible.
     * @param clazz The class of the method.
     * @param method The method name.
     * @param params The parameters of the method.
     * @return The specified method.
     * @throws NoSuchMethodException iff method doesn't exist.
     */
    public static Method getMethod(Class<?> clazz, String method, Class<?>... params)
            throws NoSuchMethodException {
        Method m = clazz.getMethod(method, params);
        m.setAccessible(true);
        return m;
    }

    /**
     * Retrieves fields from a class based on the specified FieldParams.
     * @param clazz The class of the fields.
     * @param fieldParams The fields which will be collected.
     * @return A map with key the field name and value the actual field.
     */
    public static Map<String, Field> getAllFields(Class<?> clazz, FieldParam... fieldParams) {
        Map<String, Field> map = new HashMap<>();
        for (FieldParam fieldParam : fieldParams) {
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

    /**
     * Retrieves methods from a class based on the specified MethodParams.
     * @param clazz The class of the methods.
     * @param methodParams The methods which will be collected.
     * @return A map with key the method name and value the actual method.
     */
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

    /**
     * Invokes a method on an instance.
     * Will return null if method not present in map.
     * @param map The map with methods.
     * @param instance The instance of the class.
     * @param methodName The name of the method.
     * @param params The parameters of the method.
     * @return The object returned by the method, or null if not present in map.
     * @throws InvocationTargetException If the method call produced an exception.
     * @throws IllegalAccessException When prohibited access to the method.
     */
    public static Object invoke(Map<String, Method> map, Object instance, String methodName, Object... params)
            throws InvocationTargetException, IllegalAccessException {
        Method method = map.get(methodName);
        if (method == null) return null;
        return method.invoke(instance, params);
    }

    /**
     * Retrieves the specified field from an object instance.
     * Returns null if the field is not in the map.
     * @param map The map with fields.
     * @param instance The instance of the class.
     * @param fieldName The field name.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static Object get(Map<String, Field> map, Object instance, String fieldName) throws IllegalAccessException {
        Field field = map.get(fieldName);
        if (field == null) return null;
        return field.get(instance);
    }

    /**
     * Sets the specified field to the specified value.
     * Will silently fail if the field is not in the map.
     * @param map The map with fields.
     * @param instance The instance of the class.
     * @param fieldName The field name.
     * @param value The value to set the field to.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static void set(Map<String, Field> map, Object instance, String fieldName, Object value)
            throws IllegalAccessException {
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
