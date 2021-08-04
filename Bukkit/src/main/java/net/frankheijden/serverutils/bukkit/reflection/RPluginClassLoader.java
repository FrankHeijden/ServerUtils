package net.frankheijden.serverutils.bukkit.reflection;

import java.lang.reflect.Field;
import java.util.Map;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import dev.frankheijden.minecraftreflection.Reflection;
import net.frankheijden.serverutils.common.utils.ReflectionUtils;
import org.bukkit.plugin.PluginLoader;

public class RPluginClassLoader {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("org.bukkit.plugin.java.PluginClassLoader");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    /**
     * Clears and closes the provided classloader.
     * @param loader The classloader instance.
     */
    public static void clearClassLoader(ClassLoader loader) {
        if (loader == null) return;
        if (reflection.getClazz().isInstance(loader)) {
            clearPluginClassLoader(loader);
        }
    }

    public static PluginLoader getLoader(ClassLoader loader) {
        if (loader == null) return null;
        return reflection.get(loader, "loader");
    }

    public static ClassLoader getLibraryLoader(ClassLoader loader) {
        if (loader == null || MinecraftReflectionVersion.MINOR <= 16) return null;
        return reflection.get(loader, "libraryLoader");
    }

    /**
     * Clears the plugin fields from the specified PluginClassLoader.
     */
    public static void clearPluginClassLoader(Object classLoader) {
        if (classLoader == null) return;

        reflection.set(classLoader, "loader", null);
        if (MinecraftReflectionVersion.MINOR > 16) {
            ReflectionUtils.doPrivilegedWithUnsafe(unsafe -> {
                Field libraryLoaderField = Reflection.getField(reflection.getClazz(), "libraryLoader");
                unsafe.putObject(classLoader, unsafe.objectFieldOffset(libraryLoaderField), null);
            });
        }
        reflection.set(classLoader, "plugin", null);
        reflection.set(classLoader, "pluginInit", null);
        getClasses(classLoader).clear();
    }

    public static Map<String, Class<?>> getClasses(Object classLoader) {
        return reflection.get(classLoader, "classes");
    }
}
