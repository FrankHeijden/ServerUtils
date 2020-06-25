package net.frankheijden.serverutils.reflection;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.set;

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

    public static void clearClassLoader(ClassLoader loader) throws IOException, IllegalAccessException {
        if (loader == null) return;
        if (isInstance(loader)) {
            clearURLClassLoader((URLClassLoader) loader);
        }
    }

    public static void clearURLClassLoader(URLClassLoader loader) throws IllegalAccessException, IOException {
        if (loader == null) return;
        set(fields, loader, "plugin", null);
        set(fields, loader, "pluginInit", null);
        loader.close();
    }
}
