package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

public class RDedicatedServerSettings {

    private static Class<?> serverSettingsClass;
    private static Map<String, Method> methods;

    static {
        try {
            serverSettingsClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerSettings",
                    ReflectionUtils.NMS));
            methods = getAllMethods(serverSettingsClass,
                    methodOf("getProperties", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(Object options)throws ReflectiveOperationException {
        return serverSettingsClass.getDeclaredConstructor(Class.forName("joptsimple.OptionSet")).newInstance(options);
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
