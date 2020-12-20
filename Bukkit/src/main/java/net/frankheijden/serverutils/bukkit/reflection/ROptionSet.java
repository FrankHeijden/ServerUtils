package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class ROptionSet {

    private static final MinecraftReflection reflection = MinecraftReflection.of("joptsimple.OptionSet");

    public static MinecraftReflection getReflection() {
        return reflection;
    }
}
