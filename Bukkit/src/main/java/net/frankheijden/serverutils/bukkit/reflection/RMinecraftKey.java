package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import org.bukkit.plugin.Plugin;

public class RMinecraftKey {

    private static final MinecraftReflection reflection;

    static {
        if (MinecraftReflectionVersion.MINOR >= 17) {
            reflection = MinecraftReflection.of("net.minecraft.resources.MinecraftKey");
        } else {
            reflection = MinecraftReflection.of("net.minecraft.server.%s.MinecraftKey");
        }
    }

    private RMinecraftKey() {}

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    /**
     * Retrieves the namespace of the specified MinecraftKey instance.
     * @param instance The MinecraftKey instance.
     * @return The namespace.
     */
    public static String getNameSpace(Object instance) {
        if (MinecraftReflectionVersion.MINOR <= 13) {
            return reflection.get(instance, "a");
        } else if (MinecraftReflectionVersion.MINOR == 17) {
            return reflection.invoke(instance, "getNamespace");
        } else if (MinecraftReflectionVersion.MINOR >= 18) {
            return reflection.invoke(instance, "a");
        }
        return reflection.get(instance, "namespace");
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
