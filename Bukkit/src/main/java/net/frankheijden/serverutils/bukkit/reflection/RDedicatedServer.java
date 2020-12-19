package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.bukkit.entities.BukkitReflection.MINOR;
import static net.frankheijden.serverutils.bukkit.entities.BukkitReflection.PATCH;
import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;
import static net.frankheijden.serverutils.common.reflection.VersionParam.between;
import static net.frankheijden.serverutils.common.reflection.VersionParam.exact;
import static net.frankheijden.serverutils.common.reflection.VersionParam.max;
import static net.frankheijden.serverutils.common.reflection.VersionParam.min;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;
import net.frankheijden.serverutils.common.reflection.VersionParam;

public class RDedicatedServer {

    private static Class<?> dedicatedServerClass;
    private static Map<String, Field> fields;
    private static Map<String, Method> methods;

    static {
        try {
            dedicatedServerClass = Class.forName(String.format("net.minecraft.server.%s.DedicatedServer",
                    BukkitReflection.NMS));

            fields = getAllFields(dedicatedServerClass,
                    fieldOf("propertyManager"),
                    fieldOf("options"),
                    fieldOf("autosavePeriod"),
                    fieldOf("o", between(13, 15)));
            methods = getAllMethods(dedicatedServerClass,
                    methodOf("setSpawnAnimals", max(15), boolean.class),
                    methodOf("getSpawnAnimals"),
                    methodOf("setPVP", boolean.class),
                    methodOf("getPVP"),
                    methodOf("setAllowFlight", boolean.class),
                    methodOf("getAllowFlight"),
                    methodOf("setMotd", String.class),
                    methodOf("getMotd"),
                    methodOf("setSpawnNPCs", max(15), boolean.class),
                    methodOf("setAllowFlight", boolean.class),
                    methodOf("setResourcePack", String.class, String.class),
                    methodOf("setForceGamemode", boolean.class),
                    methodOf("n", between(13, 15), boolean.class),
                    methodOf("aZ", max(15)),
                    methodOf("aZ", min(new VersionParam.Version(16, 2))),
                    methodOf("i", min(16), boolean.class),
                    methodOf("aY", exact(new VersionParam.Version(16, 1))),
                    methodOf("getCustomRegistry", min(new VersionParam.Version(16, 2))));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Class<?> getClazz() {
        return dedicatedServerClass;
    }

    public static Object getCustomRegistry(Object dedicatedServer) throws ReflectiveOperationException {
        return invoke(methods, dedicatedServer, "getCustomRegistry");
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
            Object propertyManager;
            if (MINOR >= 16 && PATCH >= 2) {
                propertyManager = RDedicatedServerSettings.newInstance(invoke(methods, console, "getCustomRegistry"),
                        options);
            } else {
                propertyManager = RDedicatedServerSettings.newInstance(options);
            }

            set(fields, console, "propertyManager", propertyManager);
            Object config = invoke(RDedicatedServerSettings.getMethods(), propertyManager, "getProperties");
            invoke(methods, console, "setPVP", getConfigValue(config, "pvp"));
            invoke(methods, console, "setAllowFlight", getConfigValue(config, "allowFlight"));
            invoke(methods, console, "setMotd", getConfigValue(config, "motd"));
            invoke(methods, console, "setForceGamemode", getConfigValue(config, "forceGamemode"));

            Object resourcePackHash;
            if (MINOR <= 15 || (MINOR == 16 && PATCH == 1)) {
                resourcePackHash = invoke(methods, console, "aZ");
            } else {
                resourcePackHash = invoke(methods, console, "aY");
            }
            invoke(methods, console, "setResourcePack", getConfigValue(config, "resourcePack"), resourcePackHash);

            if (MINOR <= 15) {
                invoke(methods, console, "setSpawnAnimals", getConfigValue(config, "spawnAnimals"));
                invoke(methods, console, "setSpawnNPCs", getConfigValue(config, "spawnNpcs"));
                invoke(methods, console, "n", getConfigValue(config, "enforceWhitelist"));
                set(fields, console, "o", getConfigValue(config, "gamemode"));
            } else {
                invoke(methods, console, "i", getConfigValue(config, "enforceWhitelist"));
            }
        } else {
            Object config = RPropertyManager.newInstance(options);
            setConfigValue(config, console, "getSpawnAnimals", "setSpawnAnimals", "getBoolean", "spawn-animals");
            setConfigValue(config, console, "getPVP", "setPVP", "getBoolean", "pvp");
            setConfigValue(config, console, "getAllowFlight", "setAllowFlight", "getBoolean", "allow-flight");
            setConfigValue(config, console, "getMotd", "setMotd", "getString", "motd");
        }
    }

    /**
     * Reloads server.properties.
     * @throws ReflectiveOperationException Iff exception thrown regarding reflection.
     */
    public static void reloadServerProperties() throws ReflectiveOperationException {
        Object console = RCraftServer.getConsole();
        Object playerList = get(RMinecraftServer.getFields(), console, "playerList");
        Object propertyManager = get(fields, console, "propertyManager");
        Path path = RDedicatedServerSettings.getServerPropertiesPath(propertyManager);

        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(path.toFile())) {
            properties.load(in);
        } catch (IOException ex) {
            throw new ReflectiveOperationException("Unable to load server.properties", ex);
        }

        int maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
        set(RPlayerList.getFields(), playerList, "maxPlayers", maxPlayers);

        int viewDistance = Integer.parseInt(properties.getProperty("view-distance"));
        RPlayerList.setViewDistance(playerList, viewDistance);
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
