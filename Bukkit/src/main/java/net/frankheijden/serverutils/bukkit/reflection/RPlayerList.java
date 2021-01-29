package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RPlayerList {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.PlayerList");

    public static MinecraftReflection getReflection() {
        return reflection;
    }
}
