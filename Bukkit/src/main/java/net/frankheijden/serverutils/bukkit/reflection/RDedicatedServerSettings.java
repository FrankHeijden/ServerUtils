package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.ConstructorParam.constructorOf;
import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllConstructors;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

public class RDedicatedServerSettings {

    private static Class<?> serverSettingsClass;
    private static Map<String, Method> methods;
    private static Map<String, Field> fields;
    private static List<Constructor<?>> constructors;

    static {
        try {
            serverSettingsClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServerSettings",
                    BukkitReflection.NMS));
            methods = getAllMethods(serverSettingsClass,
                    methodOf("getProperties"));
            fields = getAllFields(serverSettingsClass,
                    fieldOf("path"));
            constructors = getAllConstructors(serverSettingsClass,
                    constructorOf(Class.forName("joptsimple.OptionSet")),
                    constructorOf(Class.forName(String.format("net.minecraft.server.%s.IRegistryCustom",
                            BukkitReflection.NMS)), Class.forName("joptsimple.OptionSet")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the default constructor.
     * @return The default constructor of DedicatedServerSettings.
     * @throws NoSuchMethodException If no constructor was found.
     */
    public static Constructor<?> getConstructor() throws NoSuchMethodException {
        if (constructors.size() == 0) throw new NoSuchMethodException("No constructor found for "
                + serverSettingsClass.getName());
        return constructors.get(0);
    }

    public static Object newInstance(Object options) throws ReflectiveOperationException {
        return getConstructor().newInstance(options);
    }

    public static Object newInstance(Object registry, Object options) throws ReflectiveOperationException {
        return getConstructor().newInstance(registry, options);
    }

    public static Path getServerPropertiesPath(Object instance) throws ReflectiveOperationException {
        return (Path) get(fields, instance, "path");
    }

    public static Map<String, Method> getMethods() {
        return methods;
    }
}
