package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.set;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
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
     * Remov
     * @param loader The classloader instance.
     * @throws IOException When closing the loader failed.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    public static void clearClassLoader(ClassLoader loader) throws IOException, IllegalAccessException {
        if (loader == null) return;
        if (isInstance(loader)) {
            clearUrlClassLoader(loader);
        }

        if (loader instanceof URLClassLoader) {
            ((URLClassLoader) loader).close();
        }
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
