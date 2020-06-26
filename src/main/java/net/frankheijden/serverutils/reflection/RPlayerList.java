package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

public class RPlayerList {

    private static Class<?> playerListClass;
    private static Map<String, Method> methods;

    static {
        try {
            playerListClass = Class.forName(String.format("net.minecraft.server.%s.PlayerList", ReflectionUtils.NMS));
            methods = getAllMethods(playerListClass,
                    methodOf("getIPBans", ALL_VERSIONS),
                    methodOf("getProfileBans", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
