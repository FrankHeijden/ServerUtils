package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.ClassObject;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RDedicatedServer {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.minecraft.server.%s.DedicatedServer");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    /**
     * Reloads the specified console (= DedicatedServer) instance's bukkit config.
     * @param console The console to reload.
     */
    public static void reload(Object console) {
        Object options = reflection.get(console, "options");

        if (MinecraftReflectionVersion.MINOR >= 13) {
            Object propertyManager;
            if (MinecraftReflectionVersion.isMin(16, 2)) {
                propertyManager = RDedicatedServerSettings.newInstance(reflection.invoke(console, "getCustomRegistry"),
                        options);
            } else {
                propertyManager = RDedicatedServerSettings.newInstance(options);
            }

            reflection.set(console, "propertyManager", propertyManager);
            Object config = RDedicatedServerSettings.getReflection().invoke(propertyManager, "getProperties");
            reflection.invoke(console, "setPVP", ClassObject.of(boolean.class, getConfigValue(config, "pvp")));
            reflection.invoke(console, "setAllowFlight",
                    ClassObject.of(boolean.class, getConfigValue(config, "allowFlight")));
            reflection.invoke(console, "setMotd", getConfigValue(config, "motd"));
            reflection.invoke(console, "setForceGamemode",
                    ClassObject.of(boolean.class, getConfigValue(config, "forceGamemode")));

            Object resourcePackHash;
            if (MinecraftReflectionVersion.MINOR <= 15 || MinecraftReflectionVersion.is(16, 1)) {
                resourcePackHash = reflection.invoke(console, "aZ");
            } else if (MinecraftReflectionVersion.is(16, 3)) {
                resourcePackHash = reflection.invoke(console, "ba");
            } else {
                resourcePackHash = reflection.invoke(console, "aY");
            }
            reflection.invoke(console, "setResourcePack", getConfigValue(config, "resourcePack"), resourcePackHash);

            if (MinecraftReflectionVersion.MINOR <= 15) {
                reflection.invoke(console, "setSpawnAnimals",
                        ClassObject.of(boolean.class, getConfigValue(config, "spawnAnimals")));
                reflection.invoke(console, "setSpawnNPCs",
                        ClassObject.of(boolean.class, getConfigValue(config, "spawnNpcs")));
                reflection.invoke(console, "n",
                        ClassObject.of(boolean.class, getConfigValue(config, "enforceWhitelist")));
                reflection.set(console, "o", getConfigValue(config, "gamemode"));
            } else {
                reflection.invoke(console, "i",
                        ClassObject.of(boolean.class, getConfigValue(config, "enforceWhitelist")));
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
    public static void reloadServerProperties() {
        Object console = RCraftServer.getConsole();
        Object playerList = RMinecraftServer.getReflection().get(console, MinecraftReflectionVersion.MINOR >= 13
                ? "playerList"
                : "v");
        Object propertyManager = reflection.get(console, "propertyManager");
        File file;
        if (MinecraftReflectionVersion.MINOR >= 14) {
            file = RDedicatedServerSettings.getServerPropertiesPath(propertyManager).toFile();
        } else {
            file = RPropertyManager.getReflection().get(propertyManager, "file");
        }

        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load server.properties", ex);
        }

        int maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
        RPlayerList.getReflection().set(playerList, "maxPlayers", maxPlayers);

        int viewDistance = Integer.parseInt(properties.getProperty("view-distance"));
        if (MinecraftReflectionVersion.MINOR >= 14) {
            RPlayerList.getReflection().set(playerList, "viewDistance", viewDistance);
        }
        RPlayerList.getReflection().invoke(playerList, "a", ClassObject.of(int.class, viewDistance));
    }

    public static Object getConfigValue(Object config, String key) {
        return RDedicatedServerProperties.getReflection().get(config, key);
    }

    /**
     * Sets the specified bukkit config value.
     * @param config The config instance (= PropertyManager)
     * @param console The console instance (= DedicatedServer)
     * @param getMethod The getter method for the config value.
     * @param setMethod The setter method for the config value.
     * @param configMethod The method which we call the config value upon.
     * @param key The config key.
     */
    public static void setConfigValue(Object config, Object console, String getMethod, String setMethod,
                                            String configMethod, String key) {
        Object defaultValue = reflection.invoke(console, getMethod);
        Object configValue = RPropertyManager.getReflection().invoke(config, configMethod, key, defaultValue);
        reflection.invoke(console, setMethod, configValue);
    }
}
