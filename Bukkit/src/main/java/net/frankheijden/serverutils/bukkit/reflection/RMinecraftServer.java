package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;

public class RMinecraftServer {

    private static final MinecraftReflection reflection;

    static {
        if (MinecraftReflectionVersion.MINOR >= 17) {
            reflection = MinecraftReflection.of("net.minecraft.server.MinecraftServer");
        } else {
            reflection = MinecraftReflection.of("net.minecraft.server.%s.MinecraftServer");
        }
    }

    private RMinecraftServer() {}

    public static MinecraftReflection getReflection() {
        return reflection;
    }
}
