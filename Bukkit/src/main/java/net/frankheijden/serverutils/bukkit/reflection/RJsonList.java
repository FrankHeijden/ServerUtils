package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RJsonList {

    private static Class<?> jsonListClass;
    private static Map<String, Method> methods;

    static {
        try {
            jsonListClass = Class.forName(String.format("net.minecraft.server.%s.JsonList", BukkitReflection.NMS));
            methods = getAllMethods(jsonListClass,
                    methodOf("load", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void load(Object jsonList) throws InvocationTargetException, IllegalAccessException {
        invoke(methods, jsonList, "load");
    }
}
