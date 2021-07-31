package net.frankheijden.serverutils.bungee.reflection;

import java.util.Set;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import net.md_5.bungee.api.plugin.Plugin;

public class RPluginClassLoader {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.md_5.bungee.api.plugin.PluginClassloader");

    private RPluginClassLoader() {}

    /**
     * Removes the PluginClassLoader of a specific plugin.
     */
    public static Object removePluginClassLoader(Plugin plugin) {
        Set<Object> allLoaders = reflection.get(null, "allLoaders");
        if (allLoaders == null) return null;

        Object matchingLoader = null;
        for (Object loader : allLoaders) {
            if (plugin.equals(reflection.get(loader, "plugin"))) {
                matchingLoader = loader;
                break;
            }
        }
        if (matchingLoader != null) allLoaders.remove(matchingLoader);
        return matchingLoader;
    }
}
