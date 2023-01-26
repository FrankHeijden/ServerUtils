package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RPropertyManager {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.PropertyManager");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static Object newInstance(Object options) {
        return getReflection().newInstance(options);
    }
}
