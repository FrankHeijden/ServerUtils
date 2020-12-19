package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Method;
import java.util.Map;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RPropertyManager {

    private static Class<?> propertyManagerClass;
    private static Map<String, Method> methods;

    static {
        try {
            propertyManagerClass = Class.forName(String.format("net.minecraft.server.%s.PropertyManager",
                    BukkitReflection.NMS));
            methods = getAllMethods(propertyManagerClass,
                    methodOf("getBoolean", String.class, boolean.class),
                    methodOf("getString", String.class, String.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(Object options) throws ReflectiveOperationException {
        return propertyManagerClass.getDeclaredConstructor(Class.forName("joptsimple.OptionSet")).newInstance(options);
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
