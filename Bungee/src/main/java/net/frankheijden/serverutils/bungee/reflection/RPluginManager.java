package net.frankheijden.serverutils.bungee.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.collect.Multimap;
import net.frankheijden.serverutils.common.utils.MapUtils;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

public class RPluginManager {

    private static Class<?> pluginManagerClass;
    private static Map<String, Field> fields;

    static {
        try {
            pluginManagerClass = Class.forName("net.md_5.bungee.api.plugin.PluginManager");
            fields = getAllFields(pluginManagerClass,
                    fieldOf("yaml", ALL_VERSIONS),
                    fieldOf("plugins", ALL_VERSIONS),
                    fieldOf("commandMap", ALL_VERSIONS),
                    fieldOf("toLoad", ALL_VERSIONS),
                    fieldOf("commandsByPlugin", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Clears the plugin from the PluginManager.
     * @param instance The instance of the PluginManager.
     * @param plugin The plugin to clear.
     * @throws ReflectiveOperationException Iff a reflection error happened.
     */
    @SuppressWarnings("rawtypes")
    public static void clearPlugin(Object instance, Plugin plugin) throws ReflectiveOperationException {
        String pluginName = plugin.getDescription().getName();
        MapUtils.remove((Map) get(fields, instance, "plugins"), pluginName);
        MapUtils.remove((Map) get(fields, instance, "toLoad"), pluginName);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Plugin> getPlugins(Object instance) throws ReflectiveOperationException {
        return (Map<String, Plugin>) get(fields, instance, "plugins");
    }

    public static Yaml getYaml(Object instance) throws IllegalAccessException {
        return (Yaml) get(fields, instance, "yaml");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Command> getCommands(Object instance) throws IllegalAccessException {
        return (Map<String, Command>) get(fields, instance, "commandMap");
    }

    @SuppressWarnings("unchecked")
    public static Plugin getPlugin(Object instance, Command cmd) throws IllegalAccessException {
        Object obj = get(fields, instance, "commandsByPlugin");
        Multimap<Plugin, Command> plugins = (Multimap<Plugin, Command>) obj;
        if (plugins == null) return null;

        for (Map.Entry<Plugin, Command> entry : plugins.entries()) {
            if (entry.getValue().equals(cmd)) return entry.getKey();
        }
        return null;
    }
}
