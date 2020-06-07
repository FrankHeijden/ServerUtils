package net.frankheijden.serverutils.reflection;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.*;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

public class RMinecraftKey {

    private static Class<?> minecraftKeyClass;
    private static Map<String, Field> fields;

    static {
        try {
            minecraftKeyClass = Class.forName(String.format("net.minecraft.server.%s.MinecraftKey", ReflectionUtils.NMS));
            fields = getAllFields(minecraftKeyClass,
                    fieldOf("a", max(13)),
                    fieldOf("namespace", min(14)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getNameSpace(Object instance) throws IllegalAccessException {
        if (ReflectionUtils.MINOR <= 13) {
            return (String) get(fields, instance, "a");
        }
        return (String) get(fields, instance, "namespace");
    }

    public static boolean isFrom(Object instance, Plugin plugin) throws IllegalAccessException {
        String namespace = plugin.getName().toLowerCase(Locale.ROOT);
        return namespace.equalsIgnoreCase(getNameSpace(instance));
    }

    public static Predicate<Object> matchingPluginPredicate(AtomicBoolean errorThrown, Plugin plugin) {
        return o -> {
            try {
                return RMinecraftKey.isFrom(o, plugin);
            } catch (IllegalAccessException ex) {
                if (!errorThrown.get()) {
                    ex.printStackTrace();
                    errorThrown.set(true);
                }
            }
            return false;
        };
    }
}
