package net.frankheijden.serverutils.reflection;

import org.bukkit.plugin.java.PluginClassLoader;

import java.lang.reflect.*;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

public class RPluginClassLoader {

    private static Class<?> pluginClassLoaderClass;
    private static Map<String, Field> fields;

    static {
        try {
            pluginClassLoaderClass = PluginClassLoader.class;
            fields = getAllFields(pluginClassLoaderClass,
                    fieldOf("plugin", ALL_VERSIONS),
                    fieldOf("pluginInit", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Field> getFields() {
        return fields;
    }
}
