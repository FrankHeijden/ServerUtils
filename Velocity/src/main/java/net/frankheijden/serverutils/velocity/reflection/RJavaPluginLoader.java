package net.frankheijden.serverutils.velocity.reflection;

import com.google.inject.Module;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.lang.reflect.Array;
import java.nio.file.Path;

public class RJavaPluginLoader {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.plugin.loader.java.JavaPluginLoader");

    private RJavaPluginLoader() {}

    /**
     * Constructs a new instance of a JavaPluginLoader.
     */
    public static Object newInstance(ProxyServer proxy, Path baseDirectory) {
        return reflection.newInstance(
                ClassObject.of(ProxyServer.class, proxy),
                ClassObject.of(Path.class, baseDirectory)
        );
    }

    public static PluginDescription loadPluginDescription(Object javaPluginLoader, Path source) {
        return reflection.invoke(javaPluginLoader, "loadPluginDescription", ClassObject.of(Path.class, source));
    }

    /**
     * Loads the plugin from their candidate PluginDescription.
     */
    public static PluginDescription loadPlugin(Object javaPluginLoader, PluginDescription candidate) {
        return reflection.invoke(
                javaPluginLoader,
                "loadPlugin",
                ClassObject.of(PluginDescription.class, candidate)
        );
    }

    public static Module createModule(Object javaPluginLoader, PluginContainer container) {
        return reflection.invoke(javaPluginLoader, "createModule", ClassObject.of(PluginContainer.class, container));
    }

    /**
     * Creates the plugin.
     */
    public static void createPlugin(Object javaPluginLoader, PluginContainer container, Module... modules) {
        reflection.invoke(
                javaPluginLoader,
                "createPlugin",
                ClassObject.of(PluginContainer.class, container),
                ClassObject.of(Array.newInstance(Module.class, 0).getClass(), modules)
        );
    }
}
