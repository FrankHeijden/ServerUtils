package net.frankheijden.serverutils.reflection;

import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.*;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.*;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.FieldParam.fieldOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.MethodParam.methodOf;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.VersionParam.*;

public class RCraftServer {

    private static Class<?> craftServerClass;
    private static Object craftServer;
    private static File configFile;
    private static File commandsConfigFile;
    private static SimpleCommandMap commandMap;

    private static Map<String, Field> fields;
    private static Map<String, Method> methods;

    static {
        try {
            craftServerClass = Class.forName(String.format("org.bukkit.craftbukkit.%s.CraftServer", ReflectionUtils.NMS));
            craftServer = craftServerClass.cast(Bukkit.getServer());

            commandsConfigFile = (File) getDeclaredMethod(craftServerClass, "getCommandsConfigFile").invoke(craftServer);
            configFile = (File) getDeclaredMethod(craftServerClass, "getConfigFile").invoke(craftServer);
            commandMap = (SimpleCommandMap) getDeclaredField(craftServerClass, "commandMap").get(Bukkit.getServer());

            fields = getAllFields(craftServerClass,
                    fieldOf("configuration", ALL_VERSIONS),
                    fieldOf("console", ALL_VERSIONS),
                    fieldOf("commandsConfiguration", ALL_VERSIONS),
                    fieldOf("overrideAllCommandBlockCommands", ALL_VERSIONS),
                    fieldOf("unrestrictedAdvancements", max(12)),
                    fieldOf("ignoreVanillaPermissions", min(13)),
                    fieldOf("monsterSpawn", ALL_VERSIONS),
                    fieldOf("animalSpawn", ALL_VERSIONS),
                    fieldOf("waterAnimalSpawn", ALL_VERSIONS),
                    fieldOf("ambientSpawn", ALL_VERSIONS),
                    fieldOf("warningState", ALL_VERSIONS),
                    fieldOf("minimumAPI", min(13)),
                    fieldOf("printSaveWarning", ALL_VERSIONS),
                    fieldOf("chunkGCPeriod", max(12)),
                    fieldOf("chunkGCLoadThresh", max(12)),
                    fieldOf("playerList", ALL_VERSIONS));
            methods = getAllMethods(craftServerClass,
                    methodOf("loadIcon", ALL_VERSIONS));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object getCraftServer() {
        return craftServer;
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static File getOptionsFile(String option) throws IllegalAccessException, InvocationTargetException {
        Object console = get(fields, craftServer, "console");
        Object options = get(RDedicatedServer.getFields(), console, "options");
        return (File) invoke(ROptionSet.getMethods(), options, "valueOf", option);
    }

    public static File getCommandsConfigFile() {
        return commandsConfigFile;
    }

    public static SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public static void reloadBukkitConfiguration() throws Exception {
        YamlConfiguration bukkit = YamlConfiguration.loadConfiguration(getConfigFile());
        set(fields, craftServer, "configuration", bukkit);

        Object console = get(fields, craftServer, "console");
        RDedicatedServer.reload(console);

        set(fields, craftServer, "monsterSpawn", bukkit.getInt("spawn-limits.monsters"));
        set(fields, craftServer, "animalSpawn", bukkit.getInt("spawn-limits.animals"));
        set(fields, craftServer, "waterAnimalSpawn", bukkit.getInt("spawn-limits.water-animals"));
        set(fields, craftServer, "ambientSpawn", bukkit.getInt("spawn-limits.ambient"));
        set(fields, craftServer, "warningState", Warning.WarningState.value(bukkit.getString("settings.deprecated-verbose")));
        set(fields, craftServer, "minimumAPI", bukkit.getString("settings.minimum-api"));
        set(fields, craftServer, "printSaveWarning", false);

        set(RDedicatedServer.getFields(), console, "autosavePeriod", bukkit.getInt("ticks-per.autosave"));

        set(fields, craftServer, "chunkGCPeriod", bukkit.getInt("chunk-gc.period-in-ticks"));
        set(fields, craftServer, "chunkGCLoadThresh", bukkit.getInt("chunk-gc.load-threshold"));
    }

    public static void loadIcon() throws InvocationTargetException, IllegalAccessException {
        invoke(methods, craftServer, "loadIcon");
    }

    public static void reloadCommandsConfiguration() throws IllegalAccessException, InvocationTargetException {
        Map<String, Command> map = RCommandMap.getKnownCommands(commandMap);
        Bukkit.getCommandAliases().keySet().forEach(map::remove);

        YamlConfiguration commands = YamlConfiguration.loadConfiguration(getCommandsConfigFile());
        set(fields, craftServer, "commandsConfiguration", commands);
        set(fields, craftServer, "overrideAllCommandBlockCommands", commands.getStringList("command-block-overrides").contains("*"));
        set(fields, craftServer, "ignoreVanillaPermissions", commands.getBoolean("ignore-vanilla-permissions"));
        set(fields, craftServer, "unrestrictedAdvancements", commands.getBoolean("unrestricted-advancements"));

        commandMap.registerServerAliases();
    }

    public static void reloadIPBans() throws IllegalAccessException, InvocationTargetException {
        Object playerList = get(fields, craftServer, "playerList");
        Object jsonList = invoke(RPlayerList.getMethods(), playerList, "getIPBans");
        RJsonList.load(jsonList);
    }

    public static void reloadProfileBans() throws IllegalAccessException, InvocationTargetException {
        Object playerList = get(fields, craftServer, "playerList");
        Object jsonList = invoke(RPlayerList.getMethods(), playerList, "getProfileBans");
        RJsonList.load(jsonList);
    }
}
