package net.frankheijden.serverutils.reflection;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.max;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.min;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.getAllFields;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.bukkit.plugin.Plugin;

public class RMinecraftKey {

    private static Class<?> minecraftKeyClass;
    private static Map<String, Field> fields;

    static {
        try {
            minecraftKeyClass = Class.forName(String.format("net.minecraft.server.%s.MinecraftKey",
                    ReflectionUtils.NMS));
            fields = getAllFields(minecraftKeyClass,
                    fieldOf("a", max(13)),
                    fieldOf("namespace", min(14)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the namespace of the specified MinecraftKey instance.
     * @param instance The MinecraftKey instance.
     * @return The namespace.
     * @throws IllegalAccessException When prohibited access to the field.
     */
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

    /**
     * Creates a predicate which returns true if a MinecraftKey instance comes from the specified plugin.
     * @param errorThrown Requires an atomicboolean to ensure an exception is only thrown once, if any.
     * @param plugin The plugin to match the MinecraftKey instance with.
     * @return The predicate.
     */
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
