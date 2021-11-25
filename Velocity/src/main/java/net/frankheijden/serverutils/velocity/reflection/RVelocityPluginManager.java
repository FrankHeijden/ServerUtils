package net.frankheijden.serverutils.velocity.reflection;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginManager;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Map;

public class RVelocityPluginManager {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.plugin.VelocityPluginManager");

    private RVelocityPluginManager() {}

    /**
     * Retrieves the plugin map. Key is the id of the plugin.
     */
    public static Map<String, PluginContainer> getPlugins(PluginManager manager) {
        String fieldName = "plugins";
        try {
            reflection.getClazz().getField(fieldName);
        } catch (NoSuchFieldException ex) {
            fieldName = "pluginsById";
        }

        return reflection.get(manager, fieldName);
    }

    public static Map<Object, PluginContainer> getPluginInstances(PluginManager manager) {
        return reflection.get(manager, "pluginInstances");
    }

    public static void registerPlugin(PluginManager manager, PluginContainer container) {
        reflection.invoke(manager, "registerPlugin", ClassObject.of(PluginContainer.class, container));
    }
}
