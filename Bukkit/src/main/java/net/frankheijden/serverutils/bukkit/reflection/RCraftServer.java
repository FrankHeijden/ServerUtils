package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.FieldParam.fieldOf;
import static net.frankheijden.serverutils.common.reflection.MethodParam.methodOf;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.get;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllFields;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getAllMethods;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getDeclaredField;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getDeclaredMethod;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.invoke;
import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.set;
import static net.frankheijden.serverutils.common.reflection.VersionParam.exact;
import static net.frankheijden.serverutils.common.reflection.VersionParam.max;
import static net.frankheijden.serverutils.common.reflection.VersionParam.min;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;

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
            craftServerClass = Class.forName(String.format("org.bukkit.craftbukkit.%s.CraftServer",
                    BukkitReflection.NMS));
            craftServer = craftServerClass.cast(Bukkit.getServer());

            commandsConfigFile = (File) getDeclaredMethod(craftServerClass,
                    "getCommandsConfigFile").invoke(craftServer);
            configFile = (File) getDeclaredMethod(craftServerClass, "getConfigFile").invoke(craftServer);
            commandMap = (SimpleCommandMap) getDeclaredField(craftServerClass, "commandMap").get(Bukkit.getServer());

            fields = getAllFields(craftServerClass,
                    fieldOf("configuration"),
                    fieldOf("console"),
                    fieldOf("commandsConfiguration"),
                    fieldOf("overrideAllCommandBlockCommands"),
                    fieldOf("unrestrictedAdvancements", exact(12)),
                    fieldOf("ignoreVanillaPermissions", min(13)),
                    fieldOf("monsterSpawn"),
                    fieldOf("animalSpawn"),
                    fieldOf("waterAnimalSpawn"),
                    fieldOf("ambientSpawn"),
                    fieldOf("warningState"),
                    fieldOf("minimumAPI", min(14)),
                    fieldOf("printSaveWarning"),
                    fieldOf("chunkGCPeriod", max(12)),
                    fieldOf("chunkGCLoadThresh", max(12)),
                    fieldOf("playerList"));
            methods = getAllMethods(craftServerClass,
                    methodOf("loadIcon"),
                    methodOf("syncCommands"));
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

    /**
     * Retrieves the options file from a key.
     * @param option The option key.
     * @return The associated file.
     * @throws InvocationTargetException If the method call produced an exception.
     * @throws IllegalAccessException When prohibited access to the method.
     */
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

    public static void syncCommands() throws InvocationTargetException, IllegalAccessException {
        invoke(methods, craftServer, "syncCommands");
    }

    /**
     * Reloads the bukkit configuration.
     * @throws ReflectiveOperationException Iff exception thrown regarding reflection.
     */
    public static void reloadBukkitConfiguration() throws ReflectiveOperationException {
        YamlConfiguration bukkit = YamlConfiguration.loadConfiguration(getConfigFile());
        set(fields, craftServer, "configuration", bukkit);

        Object console = get(fields, craftServer, "console");
        RDedicatedServer.reload(console);

        set(fields, craftServer, "monsterSpawn", bukkit.getInt("spawn-limits.monsters"));
        set(fields, craftServer, "animalSpawn", bukkit.getInt("spawn-limits.animals"));
        set(fields, craftServer, "waterAnimalSpawn", bukkit.getInt("spawn-limits.water-animals"));
        set(fields, craftServer, "ambientSpawn", bukkit.getInt("spawn-limits.ambient"));
        set(fields, craftServer, "warningState",
                Warning.WarningState.value(bukkit.getString("settings.deprecated-verbose")));
        set(fields, craftServer, "minimumAPI", bukkit.getString("settings.minimum-api"));
        set(fields, craftServer, "printSaveWarning", false);

        set(RDedicatedServer.getFields(), console, "autosavePeriod", bukkit.getInt("ticks-per.autosave"));

        set(fields, craftServer, "chunkGCPeriod", bukkit.getInt("chunk-gc.period-in-ticks"));
        set(fields, craftServer, "chunkGCLoadThresh", bukkit.getInt("chunk-gc.load-threshold"));
    }

    public static void loadIcon() throws InvocationTargetException, IllegalAccessException {
        invoke(methods, craftServer, "loadIcon");
    }

    /**
     * Reloads the commands.yml file.
     * @throws InvocationTargetException If the method call produced an exception.
     * @throws IllegalAccessException When prohibited access to the method.
     */
    public static void reloadCommandsConfiguration() throws IllegalAccessException, InvocationTargetException {
        Map<String, Command> map = RCommandMap.getKnownCommands(commandMap);
        Bukkit.getCommandAliases().keySet().forEach(map::remove);

        YamlConfiguration commands = YamlConfiguration.loadConfiguration(getCommandsConfigFile());
        set(fields, craftServer, "commandsConfiguration", commands);
        set(fields, craftServer, "overrideAllCommandBlockCommands",
                commands.getStringList("command-block-overrides").contains("*"));
        set(fields, craftServer, "ignoreVanillaPermissions",
                commands.getBoolean("ignore-vanilla-permissions"));
        set(fields, craftServer, "unrestrictedAdvancements",
                commands.getBoolean("unrestricted-advancements"));

        commandMap.registerServerAliases();
    }

    /**
     * Reloads the ip-bans file.
     * @throws InvocationTargetException If the method call produced an exception.
     * @throws IllegalAccessException When prohibited access to the method.
     */
    public static void reloadIpBans() throws IllegalAccessException, InvocationTargetException {
        Object playerList = get(fields, craftServer, "playerList");
        Object jsonList = invoke(RPlayerList.getMethods(), playerList, "getIPBans");
        RJsonList.load(jsonList);
    }

    /**
     * Reloads the profile bans file.
     * @throws InvocationTargetException If the method call produced an exception.
     * @throws IllegalAccessException When prohibited access to the method.
     */
    public static void reloadProfileBans() throws IllegalAccessException, InvocationTargetException {
        Object playerList = get(fields, craftServer, "playerList");
        Object jsonList = invoke(RPlayerList.getMethods(), playerList, "getProfileBans");
        RJsonList.load(jsonList);
    }
}
