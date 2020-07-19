package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;

import java.lang.reflect.Field;
import java.util.Map;

public class RPluginClassLoader {

    private static Class<?> pluginClassLoaderClass;
    private static Map<String, Field> fields;

    static {
        try {
            pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            fields = getAllFields(pluginClassLoaderClass,
                    fieldOf("plugin"),
                    fieldOf("pluginInit"),
                    fieldOf("classes"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isInstance(Object obj) {
        return pluginClassLoaderClass.isInstance(obj);
    }

    /**
     * Clears and closes the provided classloader.
     * @param loader The classloader instance.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static void clearClassLoader(ClassLoader loader) throws IllegalAccessException {
        if (loader == null) return;
        if (isInstance(loader)) {
            clearPluginClassLoader(loader);
        }
    }

    /**
     * Clears the plugin fields from the specified PluginClassLoader.
     * @param pluginLoader The plugin loader instance.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static void clearPluginClassLoader(Object pluginLoader) throws IllegalAccessException {
        if (pluginLoader == null) return;
        set(fields, pluginLoader, "plugin", null);
        set(fields, pluginLoader, "pluginInit", null);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Class<?>> getClasses(Object pluginLoader) throws IllegalAccessException {
        return (Map<String,Class<?>>) get(fields, pluginLoader, "classes");
    }
}
