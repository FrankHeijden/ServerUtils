package net.frankheijden.serverutils.velocity.reflection;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RVelocityPluginContainer {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.plugin.loader.VelocityPluginContainer");

    private RVelocityPluginContainer() {}

    public static PluginContainer newInstance(PluginDescription description) {
        return reflection.newInstance(ClassObject.of(PluginDescription.class, description));
    }
}
