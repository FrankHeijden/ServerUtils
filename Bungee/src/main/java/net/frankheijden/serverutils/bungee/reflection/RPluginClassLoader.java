package net.frankheijden.serverutils.bungee.reflection;

import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;
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
            fields = getAllFields(loaderClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object newInstance(ProxyServer proxy, PluginDescription desc, URL... urls) throws Exception {
        return constructor.newInstance(proxy, desc, urls);
    }
}
