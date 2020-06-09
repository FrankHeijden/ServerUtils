package net.frankheijden.serverutils.reflection;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.invoke;

public class RJavaPlugin {

    private static Class<?> javaPluginClass;
    private static Map<String, Method> methods;

    static {
        try {
            javaPluginClass = JavaPlugin.class;
            methods = getAllMethods(javaPluginClass,
                    methodOf("getClassLoader", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ClassLoader getClassLoader(Object instance) throws InvocationTargetException, IllegalAccessException {
        return (ClassLoader) invoke(methods, instance, "getClassLoader");
    }
}
