package net.frankheijden.serverutils.bungee.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.Field;
import java.util.Map;

public class RPlugin {

    private static Class<?> pluginClass;
    private static Map<String, Field> fields;

    static {
        try {
            pluginClass = Class.forName("net.md_5.bungee.api.plugin.Plugin");
            fields = getAllFields(pluginClass,
                    fieldOf("plugins", ALL_VERSIONS),
                    fieldOf("toLoad", ALL_VERSIONS),
                    fieldOf("commandsByPlugin", ALL_VERSIONS),
                    fieldOf("listenersByPlugin", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
