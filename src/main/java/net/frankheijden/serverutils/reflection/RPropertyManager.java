package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

public class RPropertyManager {

    private static Class<?> propertyManagerClass;
    private static Map<String, Method> methods;

    static {
        try {
            propertyManagerClass = Class.forName(String.format("net.minecraft.server.%s.PropertyManager",
                    ReflectionUtils.NMS));
            methods = getAllMethods(propertyManagerClass,
                    methodOf("getBoolean", ALL_VERSIONS, String.class, boolean.class),
                    methodOf("getString", ALL_VERSIONS, String.class, String.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(Object options) throws ReflectiveOperationException {
        return propertyManagerClass.getDeclaredConstructor(Class.forName("joptsimple.OptionSet")).newInstance(options);
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
