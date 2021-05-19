package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import dev.frankheijden.minecraftreflection.exceptions.MinecraftReflectionException;
import org.bukkit.plugin.java.JavaPluginLoader;

public class RJavaPluginLoader {

    private RJavaPluginLoader() {}

    private static final MinecraftReflection reflection = MinecraftReflection.of(JavaPluginLoader.class);

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    /**
     * Removes the given classes from the JavaPluginLoader instance.
     * @param instance The instance.
     * @param list The list of classpaths.
     */
    public static void removeClasses(Object instance, Collection<? extends String> list) {
        Map<String, Class<?>> classes = getFieldIfExists(instance, "classes");
        if (classes != null) {
            for (String key : list) {
                classes.remove(key);
            }
        }

        Map<String, Integer> classLoadLockCount = getFieldIfExists(instance, "classLoadLockCount");
        if (classLoadLockCount != null) {
            for (String key : list) {
                classLoadLockCount.remove(key);
            }
        }

        Map<String, ReentrantReadWriteLock> classLoadLock = getFieldIfExists(instance, "classLoadLock");
        if (classLoadLock != null) {
            for (String key : list) {
                classLoadLock.remove(key);
            }
        }
    }

    private static <T> T getFieldIfExists(Object instance, String field) {
        try {
            return reflection.get(instance, field);
        } catch (MinecraftReflectionException ex) {
            if (ex.getCause() instanceof NoSuchFieldException) {
                return null;
            } else {
                throw ex;
            }
        }
    }
}
