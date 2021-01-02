package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import java.io.File;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class RCraftServer {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("org.bukkit.craftbukkit.%s.CraftServer");

    public static MinecraftReflection getReflection() {
        return reflection;
    }

    public static File getConfigFile() {
        return reflection.invoke(Bukkit.getServer(), "getConfigFile");
    }

    /**
     * Retrieves the options file from a key.
     * @param option The option key.
     * @return The associated file.
     */
    public static File getOptionsFile(String option) {
        Object options = reflection.get(getConsole(), "options");
        return reflection.invoke(options, "valueOf", option);
    }

    public static File getCommandsConfigFile() {
        return reflection.invoke(Bukkit.getServer(), "getCommandsConfigFile");
    }

    public static SimpleCommandMap getCommandMap() {
        return reflection.get(Bukkit.getServer(), "commandMap");
    }

    public static void syncCommands() {
        if (MinecraftReflectionVersion.MINOR >= 13) reflection.invoke(Bukkit.getServer(), "syncCommands");
    }

    public static Object getConsole() {
        return reflection.get(Bukkit.getServer(), "console");
    }

    /**
     * Reloads the bukkit configuration.
     */
    public static void reloadBukkitConfiguration() {
        YamlConfiguration bukkit = YamlConfiguration.loadConfiguration(getConfigFile());
        reflection.set(Bukkit.getServer(), "configuration", bukkit);

        RDedicatedServer.reload(getConsole());

        reflection.set(Bukkit.getServer(), "monsterSpawn", bukkit.getInt("spawn-limits.monsters"));
        reflection.set(Bukkit.getServer(), "animalSpawn", bukkit.getInt("spawn-limits.animals"));
        reflection.set(Bukkit.getServer(), "waterAnimalSpawn", bukkit.getInt("spawn-limits.water-animals"));
        reflection.set(Bukkit.getServer(), "ambientSpawn", bukkit.getInt("spawn-limits.ambient"));
        reflection.set(Bukkit.getServer(), "warningState",
                Warning.WarningState.value(bukkit.getString("settings.deprecated-verbose")));
        if (MinecraftReflectionVersion.isMin(14))
            reflection.set(Bukkit.getServer(), "minimumAPI", bukkit.getString("settings.minimum-api"));
        reflection.set(Bukkit.getServer(), "printSaveWarning", false);
        reflection.set(Bukkit.getServer(), "monsterSpawn", bukkit.getInt("spawn-limits.monsters"));
        reflection.set(Bukkit.getServer(), "monsterSpawn", bukkit.getInt("spawn-limits.monsters"));
        reflection.set(Bukkit.getServer(), "monsterSpawn", bukkit.getInt("spawn-limits.monsters"));
        if (MinecraftReflectionVersion.isMax(12)) {
            reflection.set(Bukkit.getServer(), "chunkGCPeriod", bukkit.getInt("chunk-gc.period-in-ticks"));
            reflection.set(Bukkit.getServer(), "chunkGCLoadThresh", bukkit.getInt("chunk-gc.load-threshold"));
        }

        RDedicatedServer.getReflection().set(getConsole(), "autosavePeriod", bukkit.getInt("ticks-per.autosave"));
    }

    public static void loadIcon() {
        reflection.invoke(Bukkit.getServer(), "loadIcon");
    }

    /**
     * Reloads the commands.yml file.
     */
    public static void reloadCommandsConfiguration() {
        SimpleCommandMap commandMap = getCommandMap();
        Map<String, Command> map = RCommandMap.getKnownCommands(commandMap);

        for (String alias : Bukkit.getCommandAliases().keySet()) {
            Command aliasCommand = map.remove(alias);
            if (aliasCommand == null) continue;

            aliasCommand.unregister(commandMap);
        }

        YamlConfiguration commands = YamlConfiguration.loadConfiguration(getCommandsConfigFile());
        reflection.set(Bukkit.getServer(), "commandsConfiguration", commands);
        reflection.set(Bukkit.getServer(), "overrideAllCommandBlockCommands",
                commands.getStringList("command-block-overrides").contains("*"));
        if (MinecraftReflectionVersion.isMin(13)) reflection.set(
                Bukkit.getServer(),
                "ignoreVanillaPermissions",
                commands.getBoolean("ignore-vanilla-permissions")
        );
        if (MinecraftReflectionVersion.is(12)) reflection.set(
                Bukkit.getServer(),
                "unrestrictedAdvancements",
                commands.getBoolean("unrestricted-advancements")
        );

        commandMap.registerServerAliases();
        RCraftServer.syncCommands();
    }

    /**
     * Reloads the ip-bans file.
     */
    public static void reloadIpBans() {
        Object playerList = reflection.get(Bukkit.getServer(), "playerList");
        Object jsonList = RPlayerList.getReflection().invoke(playerList, "getIPBans");
        RJsonList.load(jsonList);
    }

    /**
     * Reloads the profile bans file.
     */
    public static void reloadProfileBans() {
        Object playerList = reflection.get(Bukkit.getServer(), "playerList");
        Object jsonList = RPlayerList.getReflection().invoke(playerList, "getProfileBans");
        RJsonList.load(jsonList);
    }
}
