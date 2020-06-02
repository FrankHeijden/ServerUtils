package net.frankheijden.serverutils.reflection;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

public class RSimplePluginManager {

    private static Class<?> simplePluginManagerClass;
    private static Map<String, Field> fields;

    static {
        try {
            simplePluginManagerClass = SimplePluginManager.class;
            fields = getAllFields(simplePluginManagerClass,
                    fieldOf("plugins", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Plugin> getPlugins(Object manager) throws IllegalAccessException {
        return (List<Plugin>) fields.get("plugins").get(manager);
    }
}
