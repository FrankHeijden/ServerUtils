package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;

public class RSimplePluginManager {

    private static Class<?> simplePluginManagerClass;
    private static Map<String, Field> fields;

    static {
        try {
            simplePluginManagerClass = SimplePluginManager.class;
            fields = getAllFields(simplePluginManagerClass,
                    fieldOf("plugins", ALL_VERSIONS),
                    fieldOf("lookupNames", ALL_VERSIONS),
                    fieldOf("fileAssociations", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<Pattern, PluginLoader> getFileAssociations(Object manager) throws IllegalAccessException {
        return (Map<Pattern, PluginLoader>) get(fields, manager, "fileAssociations");
    }

    @SuppressWarnings("unchecked")
    public static List<Plugin> getPlugins(Object manager) throws IllegalAccessException {
        return (List<Plugin>) get(fields, manager, "plugins");
    }

    /**
     * Removes the lookup name of the plugin.
     * This ensures the plugin cannot be found anymore in Bukkit#getPlugin(String name).
     * @param manager The SimplePluginManager instance to remove the lookup name from.
     * @param name The name of the plugin to remove.
     * @throws IllegalAccessException When prohibited access to the field.
     */
    @SuppressWarnings("unchecked")
    public static void removeLookupName(Object manager, String name) throws IllegalAccessException {
        Map<String, Plugin> lookupNames = (Map<String, Plugin>) get(fields, manager, "lookupNames");
        if (lookupNames == null) return;
        lookupNames.remove(name.replace(' ', '_'));
        lookupNames.remove(name.replace(' ', '_').toLowerCase(Locale.ENGLISH)); // Paper
    }
}
