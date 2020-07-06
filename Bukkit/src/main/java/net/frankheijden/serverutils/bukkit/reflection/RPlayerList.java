package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RPlayerList {

    private static Class<?> playerListClass;
    private static Map<String, Method> methods;

    static {
        try {
            playerListClass = Class.forName(String.format("net.minecraft.server.%s.PlayerList", BukkitReflection.NMS));
            methods = getAllMethods(playerListClass,
                    methodOf("getIPBans"),
                    methodOf("getProfileBans"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
