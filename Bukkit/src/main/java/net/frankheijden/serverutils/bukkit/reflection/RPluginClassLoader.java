package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import java.util.Map;
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
        if (loader == null && MinecraftReflectionVersion.MINOR <= 16) return null;
        return reflection.get(loader, "libraryLoader");
    }

    /**
     * Clears the plugin fields from the specified PluginClassLoader.
     * @param pluginLoader The plugin loader instance.
     */
    public static void clearPluginClassLoader(Object pluginLoader) {
        if (pluginLoader == null) return;

        reflection.set(pluginLoader, "plugin", null);
        reflection.set(pluginLoader, "pluginInit", null);
    }

    public static Map<String, Class<?>> getClasses(Object pluginLoader) {
        return reflection.get(pluginLoader, "classes");
    }
}
