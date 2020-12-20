package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.nio.file.Path;

public class RDedicatedServerSettings {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.DedicatedServerSettings");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static Object newInstance(Object options) {
        return reflection.newInstance(options);
    }

    /**
     * Initiates a new instance of DedicatedServerSettings.
     */
    public static Object newInstance(Object registry, Object options) {
        return reflection.newInstance(
                ClassObject.of(RIRegistryCustom.getReflection().getClazz(), registry),
                ClassObject.of(options)
        );
    }

    public static Path getServerPropertiesPath(Object instance) {
        return reflection.get(instance, "path");
    }
}
