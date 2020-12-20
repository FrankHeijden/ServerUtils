package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RPlayerList {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.PlayerList");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static void setViewDistance(Object instance, int viewDistance) {
        reflection.set(instance, "viewDistance", viewDistance);
        reflection.invoke(instance, "a", ClassObject.of(int.class, viewDistance));
    }
}
