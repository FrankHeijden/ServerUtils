package net.frankheijden.serverutils.reflection;

import java.lang.reflect.Method;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;

public class RDedicatedServerSettings {

    private static Class<?> dedicatedServerSettingsClass;
    private static Map<String, Method> methods;

    static {
        try {
            dedicatedServerSettingsClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerSettings", ReflectionUtils.NMS));
            methods = getAllMethods(dedicatedServerSettingsClass,
                    methodOf("getProperties", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(Object options) throws Exception {
        return dedicatedServerSettingsClass.getDeclaredConstructor(Class.forName("joptsimple.OptionSet")).newInstance(options);
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
