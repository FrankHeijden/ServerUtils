package net.frankheijden.serverutils.bungee.reflection;

import java.net.URL;
import java.util.Set;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class RPluginClassLoader {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.md_5.bungee.api.plugin.PluginClassloader");

    /**
     * Creates a new instance of a PluginClassLoader from given parameters.
     */
    public static Object newInstance(ProxyServer proxy, PluginDescription desc, URL... urls) {
        return reflection.newInstance(
                ClassObject.of(ProxyServer.class, proxy),
                ClassObject.of(PluginDescription.class, desc),
                ClassObject.of(URL[].class, urls)
        );
    }

    /**
     * Retrieves the PluginClassLoader of a specific plugin.
     * @param plugin The plugin to lookup the PluginClassLoader for.
     * @return The PluginClassLoader.
     */
    public static Object getPluginClassLoader(Plugin plugin) {
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
