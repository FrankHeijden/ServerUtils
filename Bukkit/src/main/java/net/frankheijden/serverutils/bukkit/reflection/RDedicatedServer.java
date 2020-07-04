package net.frankheijden.serverutils.bukkit.reflection;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;

import static net.frankheijden.serverutils.bukkit.entities.BukkitReflection.MINOR;
import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;
import static net.frankheijden.serverutils.common.reflection.VersionParam.ALL_VERSIONS;
import static net.frankheijden.serverutils.common.reflection.VersionParam.min;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RDedicatedServer {

    private static Class<?> dedicatedServerClass;
    private static Map<String, Field> fields;
    private static Map<String, Method> methods;

    static {
        try {
            dedicatedServerClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServer",
                    BukkitReflection.NMS));

            fields = getAllFields(dedicatedServerClass,
                    fieldOf("propertyManager", ALL_VERSIONS),
                    fieldOf("options", ALL_VERSIONS),
                    fieldOf("autosavePeriod", ALL_VERSIONS),
                    fieldOf("o", min(13)));
            methods = getAllMethods(dedicatedServerClass,
                    methodOf("setSpawnAnimals", ALL_VERSIONS, boolean.class),
                    methodOf("getSpawnAnimals", ALL_VERSIONS),
                    methodOf("setPVP", ALL_VERSIONS, boolean.class),
                    methodOf("getPVP", ALL_VERSIONS),
                    methodOf("setAllowFlight", ALL_VERSIONS, boolean.class),
                    methodOf("getAllowFlight", ALL_VERSIONS),
                    methodOf("setMotd", ALL_VERSIONS, String.class),
                    methodOf("getMotd", ALL_VERSIONS),
                    methodOf("setSpawnNPCs", ALL_VERSIONS, boolean.class),
                    methodOf("setAllowFlight", ALL_VERSIONS, boolean.class),
                    methodOf("setResourcePack", ALL_VERSIONS, String.class, String.class),
                    methodOf("setForceGamemode", ALL_VERSIONS, boolean.class),
                    methodOf("n", min(13), boolean.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Field> getFields() {
        return fields;
    }

    /**
     * Reloads the specified console (= DedicatedServer) instance's bukkit config.
     * @param console The console to reload.
     * @throws ReflectiveOperationException Iff exception thrown regarding reflection.
     */
    public static void reload(Object console) throws ReflectiveOperationException {
        Object options = get(fields, console, "options");

        if (MINOR >= 13) {
            Object propertyManager = RDedicatedServerSettings.newInstance(options);
            set(fields, console, "propertyManager", propertyManager);
            Object config = invoke(RDedicatedServerSettings.getMethods(), propertyManager, "getProperties");
            invoke(methods, console, "setSpawnAnimals", getConfigValue(config, "spawnAnimals"));
            invoke(methods, console, "setSpawnNPCs", getConfigValue(config, "spawnNpcs"));
            invoke(methods, console, "setPVP", getConfigValue(config, "pvp"));
            invoke(methods, console, "setAllowFlight", getConfigValue(config, "allowFlight"));
            invoke(methods, console, "setResourcePack", getConfigValue(config, "resourcePack"),
                    invoke(methods, console, "aZ"));
            invoke(methods, console, "setMotd", getConfigValue(config, "motd"));
            invoke(methods, console, "setForceGamemode", getConfigValue(config, "forceGamemode"));
            invoke(methods, console, "n", getConfigValue(config, "enforceWhitelist"));
            set(fields, console, "o", getConfigValue(config, "gamemode"));
        } else {
            Object config = RPropertyManager.newInstance(options);
            setConfigValue(config, console, "getSpawnAnimals", "setSpawnAnimals", "getBoolean", "spawn-animals");
            setConfigValue(config, console, "getPVP", "setPVP", "getBoolean", "pvp");
            setConfigValue(config, console, "getAllowFlight", "setAllowFlight", "getBoolean", "allow-flight");
            setConfigValue(config, console, "getMotd", "setMotd", "getString", "motd");
        }
    }

    public static Object getConfigValue(Object config, String key) throws IllegalAccessException {
        return get(RDedicatedServerProperties.getFields(), config, key);
    }

    /**
     * Sets the specified bukkit config value.
     * @param config The config instance (= PropertyManager)
     * @param console The console instance (= DedicatedServer)
     * @param getMethod The getter method for the config value.
     * @param setMethod The setter method for the config value.
     * @param configMethod The method which we call the config value upon.
     * @param key The config key.
     * @throws InvocationTargetException If the method call produced an exception.
     * @throws IllegalAccessException When prohibited access to the method.
     */
    public static void setConfigValue(Object config, Object console, String getMethod, String setMethod,
                                            String configMethod, String key)
            throws InvocationTargetException, IllegalAccessException {
        Object defaultValue = invoke(methods, console, getMethod);
        Object configValue = invoke(RPropertyManager.getMethods(), config, configMethod, key, defaultValue);
        invoke(methods, console, setMethod, configValue);
    }
}
