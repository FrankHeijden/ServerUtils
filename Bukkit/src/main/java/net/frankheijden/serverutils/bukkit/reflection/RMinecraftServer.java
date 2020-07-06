package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RMinecraftServer {

    private static Class<?> minecraftServerClass;
    private static Map<String, Method> methods;

    static {
        try {
            minecraftServerClass = Class.forName(String.format("net.minecraft.server.%s.MinecraftServer",
                    BukkitReflection.NMS));
            methods = getAllMethods(minecraftServerClass,
                    methodOf("getServer"),
                    methodOf("getCraftingManager"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
