package net.frankheijden.serverutils.bungee.reflection;

import com.google.common.collect.Multimap;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Map;
import java.util.Stack;
import net.frankheijden.serverutils.common.utils.MapUtils;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.yaml.snakeyaml.Yaml;

public class RPluginManager {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.md_5.bungee.api.plugin.PluginManager");

    private RPluginManager() {}

    /**
     * Clears the plugin from the PluginManager.
     * @param instance The instance of the PluginManager.
     * @param plugin The plugin to clear.
     */
    public static void clearPlugin(Object instance, Plugin plugin) {
        String pluginName = plugin.getDescription().getName();
        MapUtils.remove(reflection.get(instance, "plugins"), pluginName);
        MapUtils.remove(reflection.get(instance, "toLoad"), pluginName);
    }

    public static Yaml getYaml(Object instance) {
        return reflection.get(instance, "yaml");
    }

    public static Map<String, Command> getCommands(Object instance) throws IllegalAccessException {
        return reflection.get(instance, "commandMap");
    }

    public static Map<String, PluginDescription> getToLoad(Object pluginManager) {
        return reflection.get(pluginManager, "toLoad");
    }

    public static void setToLoad(Object pluginManager, Map<String, PluginDescription> toLoad) {
        reflection.set(pluginManager, "toLoad", toLoad);
    }

    /**
     * Enables a plugin.
     */
    public static boolean enablePlugin(
            Object pluginManager,
            Map<PluginDescription, Boolean> pluginStatuses,
            Stack<PluginDescription> dependStack,
            PluginDescription plugin
    ) {
        return reflection.invoke(pluginManager, "enablePlugin",
                ClassObject.of(Map.class, pluginStatuses),
                ClassObject.of(Stack.class, dependStack),
                ClassObject.of(PluginDescription.class, plugin)
        );
    }

    /**
     * Retrieves the registered plugin of the command.
     * @param instance The PluginManager instance.
     * @param cmd The command to check the plugin of.
     * @return The plugin of the command.
     */
    public static Plugin getPlugin(Object instance, Command cmd) {
        Multimap<Plugin, Command> plugins = reflection.get(instance, "commandsByPlugin");
        if (plugins == null) return null;

        for (Map.Entry<Plugin, Command> entry : plugins.entries()) {
            if (entry.getValue().equals(cmd)) return entry.getKey();
        }
        return null;
    }
}
