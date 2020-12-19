package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RMinecraftServer {

    private static Class<?> minecraftServerClass;
    private static Map<String, Method> methods;
    private static Map<String, Field> fields;

    static {
        try {
            minecraftServerClass = Class.forName(String.format("net.minecraft.server.%s.MinecraftServer",
                    BukkitReflection.NMS));
            methods = getAllMethods(minecraftServerClass,
                    methodOf("getServer"),
                    methodOf("getCraftingManager"));
            fields = getAllFields(minecraftServerClass,
                    fieldOf("playerList"));
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
}
