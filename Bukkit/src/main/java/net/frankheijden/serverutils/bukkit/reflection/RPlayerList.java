package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RPlayerList {

    private static Class<?> playerListClass;
    private static Map<String, Method> methods;
    private static Map<String, Field> fields;

    static {
        try {
            playerListClass = Class.forName(String.format("net.minecraft.server.%s.PlayerList", BukkitReflection.NMS));
            methods = getAllMethods(playerListClass,
                    methodOf("getIPBans"),
                    methodOf("getProfileBans"),
                    methodOf("a", int.class));
            fields = getAllFields(playerListClass,
                    fieldOf("maxPlayers"),
                    fieldOf("viewDistance"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }

    public static Map<String, Field> getFields() {
        return fields;
    }

    public static void setViewDistance(Object instance, int viewDistance) throws ReflectiveOperationException {
        set(fields, instance, "viewDistance", viewDistance);
        invoke(methods, instance, "a", viewDistance);
    }
}
