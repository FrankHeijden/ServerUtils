package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class RJavaPlugin {

    private static Class<?> javaPluginClass;
    private static Map<String, Method> methods;

    static {
        try {
            javaPluginClass = JavaPlugin.class;
            methods = getAllMethods(javaPluginClass,
                    methodOf("getClassLoader"),
                    methodOf("getFile"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ClassLoader getClassLoader(Object instance) throws InvocationTargetException, IllegalAccessException {
        return (ClassLoader) invoke(methods, instance, "getClassLoader");
    }

    public static File getFile(Object instance) throws ReflectiveOperationException {
        return (File) invoke(methods, instance, "getFile");
    }
}
