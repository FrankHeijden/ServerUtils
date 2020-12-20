package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RIRegistryCustom {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.IRegistryCustom");

    public static MinecraftReflection getReflection() {
        return reflection;
    }
}
