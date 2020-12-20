package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RDedicatedServerProperties {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.DedicatedServerProperties");

    public static MinecraftReflection getReflection() {
        return reflection;
    }
}
