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

    public static Map<String, PluginContainer> getPlugins(PluginManager manager) {
        return reflection.get(manager, "plugins");
    }

    public static Map<Object, PluginContainer> getPluginInstances(PluginManager manager) {
        return reflection.get(manager, "pluginInstances");
    }

    public static void registerPlugin(PluginManager manager, PluginContainer container) {
        reflection.invoke(manager, "registerPlugin", ClassObject.of(PluginContainer.class, container));
    }
}
