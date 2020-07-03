package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.Map;

public class RPluginClassLoader {

    private static Class<?> pluginClassLoaderClass;
    private static Map<String, Field> fields;

    static {
        try {
            pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            fields = getAllFields(pluginClassLoaderClass,
                    fieldOf("plugin", ALL_VERSIONS),
                    fieldOf("pluginInit", ALL_VERSIONS));
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
     * @return The Closeable object.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static Closeable clearClassLoader(ClassLoader loader) throws IllegalAccessException {
        if (loader == null) return null;
        if (isInstance(loader)) {
            clearUrlClassLoader(loader);
        }

        if (loader instanceof Closeable) return (Closeable) loader;
        return null;
    }

    /**
     * Clears the plugin fields from the specified PluginClassLoader.
     * @param pluginLoader The plugin loader instance.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static void clearUrlClassLoader(Object pluginLoader) throws IllegalAccessException {
        if (pluginLoader == null) return;
        set(fields, pluginLoader, "plugin", null);
        set(fields, pluginLoader, "pluginInit", null);
    }
}
