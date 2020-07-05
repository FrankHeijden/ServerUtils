package net.frankheijden.serverutils.bungee.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class RPluginClassLoader {

    private static Class<?> loaderClass;
    private static Map<String, Field> fields;
    private static Constructor<?> constructor;

    static {
        try {
            loaderClass = Class.forName("net.md_5.bungee.api.plugin.PluginClassloader");
            constructor = loaderClass.getDeclaredConstructor(ProxyServer.class, PluginDescription.class, URL[].class);
            constructor.setAccessible(true);
            fields = getAllFields(loaderClass,
                    fieldOf("allLoaders", ALL_VERSIONS),
                    fieldOf("plugin", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(ProxyServer proxy, PluginDescription desc, URL... urls) throws Exception {
        return constructor.newInstance(proxy, desc, urls);
    }

    /**
     * Retrieves the PluginClassLoader of a specific plugin.
     * @param plugin The plugin to lookup the PluginClassLoader for.
     * @return The PluginClassLoader.
     * @throws ReflectiveOperationException Iff a reflection error occurred.
     */
    @SuppressWarnings("unchecked")
    public static Object getPluginClassLoader(Plugin plugin) throws ReflectiveOperationException {
        Set<Object> allLoaders = (Set<Object>) get(fields, null, "allLoaders");
        if (allLoaders == null) return null;

        Object matchingLoader = null;
        for (Object loader : allLoaders) {
            if (plugin.equals(get(fields, loader, "plugin"))) {
                matchingLoader = loader;
                break;
            }
        }
        if (matchingLoader != null) allLoaders.remove(matchingLoader);
        return matchingLoader;
    }
}
