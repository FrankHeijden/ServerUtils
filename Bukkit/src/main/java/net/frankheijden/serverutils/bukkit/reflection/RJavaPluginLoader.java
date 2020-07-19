package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.bukkit.plugin.java.JavaPluginLoader;

public class RJavaPluginLoader {

    private static Class<?> javaPluginLoaderClass;
    private static Map<String, Field> fields;

    static {
        try {
            javaPluginLoaderClass = JavaPluginLoader.class;
            fields = getAllFields(javaPluginLoaderClass,
                    fieldOf("classes"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes the given classes from the JavaPluginLoader instance.
     * @param instance The instance.
     * @param list The list of classpaths.
     * @throws IllegalAccessException When prohibited access to the method.
     */
    @SuppressWarnings("unchecked")
    public static void removeClasses(Object instance, Collection<? extends String> list) throws IllegalAccessException {
        Map<String, Class<?>> classes = (Map<String, Class<?>>) get(fields, instance, "classes");
        if (classes == null) return;

        for (String key : list) {
            classes.remove(key);
        }
    }
}
