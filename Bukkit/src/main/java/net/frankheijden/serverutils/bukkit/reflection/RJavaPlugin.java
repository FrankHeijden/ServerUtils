package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class RJavaPlugin {

    private static Class<?> javaPluginClass;
    private static Map<String, Field> fields;
    private static Map<String, Method> methods;

    static {
        try {
            javaPluginClass = JavaPlugin.class;
            fields = getAllFields(javaPluginClass,
                    fieldOf("loader"),
                    fieldOf("classLoader"));
            methods = getAllMethods(javaPluginClass,
                    methodOf("getClassLoader"),
                    methodOf("getClass"),
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

    /**
     * Clears the JavaPlugin from instances and returns the classloader associated with it.
     * @param instance The instance of the JavaPlugin.
     * @return The classloader associated with it.
     * @throws ReflectiveOperationException When a reflection error occurred.
     */
    public static Closeable clearJavaPlugin(Object instance) throws ReflectiveOperationException {
        set(fields, instance, "loader", null);
        set(fields, instance, "classLoader", null);
        Class<?> clazz = (Class<?>) invoke(methods, instance, "getClass");
        if (clazz != null && clazz.getClassLoader() instanceof Closeable) {
            return (Closeable) clazz.getClassLoader();
        }
        return null;
    }
}
