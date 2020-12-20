package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import java.util.Collection;
import java.util.Map;
import org.bukkit.plugin.java.JavaPluginLoader;

public class RJavaPluginLoader {

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
        Map<String, Class<?>> classes = reflection.get(instance, "classes");
        if (classes == null) return;

        for (String key : list) {
            classes.remove(key);
        }
    }
}
