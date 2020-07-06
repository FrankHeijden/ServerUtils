package net.frankheijden.serverutils.bungee.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;

import com.google.common.collect.Multimap;

import java.lang.reflect.Field;
import java.util.Map;

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
                    fieldOf("yaml"),
                    fieldOf("plugins"),
                    fieldOf("commandMap"),
                    fieldOf("toLoad"),
                    fieldOf("commandsByPlugin"));
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

    /**
     * Retrieves the registered plugin of the command.
     * @param instance The PluginManager instance.
     * @param cmd The command to check the plugin of.
     * @return The plugin of the command
     * @throws IllegalAccessException Iff some reflection error occurred.
     */
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
