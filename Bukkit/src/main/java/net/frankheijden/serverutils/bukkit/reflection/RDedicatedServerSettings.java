package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RDedicatedServerSettings {

    private static Class<?> serverSettingsClass;
    private static Map<String, Method> methods;

    static {
        try {
            serverSettingsClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerSettings",
                    BukkitReflection.NMS));
            methods = getAllMethods(serverSettingsClass,
                    methodOf("getProperties"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(Object options)throws ReflectiveOperationException {
        return serverSettingsClass.getDeclaredConstructor(Class.forName("joptsimple.OptionSet")).newInstance(options);
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
