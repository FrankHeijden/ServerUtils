package net.frankheijden.serverutils.reflection;

import java.lang.reflect.Method;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;

public class RMinecraftServer {

    private static Class<?> minecraftServerClass;
    private static Map<String, Method> methods;

    static {
        try {
            minecraftServerClass = Class.forName(String.format("net.minecraft.server.%s.MinecraftServer", ReflectionUtils.NMS));
            methods = getAllMethods(minecraftServerClass,
                    methodOf("getServer", ALL_VERSIONS),
                    methodOf("getCraftingManager", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
