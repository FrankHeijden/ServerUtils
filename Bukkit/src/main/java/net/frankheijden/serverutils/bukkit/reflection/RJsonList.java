package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RJsonList {

    private static final MinecraftReflection reflection = MinecraftReflection.of("net.minecraft.server.%s.JsonList");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static void load(Object jsonList) {
        reflection.invoke(jsonList, "load");
    }
}
