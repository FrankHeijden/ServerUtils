package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class RJavaPlugin {

    private static final MinecraftReflection reflection = MinecraftReflection.of(JavaPlugin.class);

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static ClassLoader getClassLoader(Object instance) {
        return reflection.invoke(instance, "getClassLoader");
    }

    public static File getFile(Object instance) {
        return reflection.invoke(instance, "getFile");
    }

    /**
     * Clears the JavaPlugin from instances and returns the classloader associated with it.
     */
    public static void clearJavaPlugin(Object instance) {
        reflection.set(instance, "loader", null);
        reflection.set(instance, "classLoader", null);
    }
}
