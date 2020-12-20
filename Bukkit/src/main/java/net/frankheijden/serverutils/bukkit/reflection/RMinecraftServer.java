package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RMinecraftServer {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.MinecraftServer");

    public static MinecraftReflection getReflection() {
        return reflection;
    }
}
