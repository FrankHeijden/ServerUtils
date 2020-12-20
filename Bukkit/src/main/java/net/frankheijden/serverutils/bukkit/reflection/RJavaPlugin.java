package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.io.Closeable;
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
     * @param instance The instance of the JavaPlugin.
     * @return The classloader associated with it.
     */
    public static Closeable clearJavaPlugin(Object instance) {
        reflection.set(instance, "loader", null);
        reflection.set(instance, "classLoader", null);
        Class<?> clazz = reflection.invoke(instance, "getClass");
        if (clazz != null && clazz.getClassLoader() instanceof Closeable) {
            return (Closeable) clazz.getClassLoader();
        }
        return null;
    }
}
