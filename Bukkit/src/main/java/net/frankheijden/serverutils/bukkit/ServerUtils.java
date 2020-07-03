package net.frankheijden.serverutils.bukkit;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.bukkit.commands.CommandPlugins;
import net.frankheijden.serverutils.bukkit.commands.CommandServerUtils;
import net.frankheijden.serverutils.bukkit.config.Config;
import net.frankheijden.serverutils.bukkit.config.Messenger;
import net.frankheijden.serverutils.bukkit.listeners.MainListener;
import net.frankheijden.serverutils.bukkit.managers.VersionManager;
import net.frankheijden.serverutils.bukkit.reflection.BukkitReflection;
import net.frankheijden.serverutils.bukkit.reflection.RCommandMap;
import net.frankheijden.serverutils.bukkit.reflection.RCraftServer;
import net.frankheijden.serverutils.bukkit.tasks.UpdateCheckerTask;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerUtils extends JavaPlugin implements CommandExecutor {

    private static final int BSTATS_METRICS_ID = 7790;

    private static ServerUtils instance;
    private PaperCommandManager commandManager;
    private CommandPlugins commandPlugins;

    public static ServerUtils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        new Metrics(this, BSTATS_METRICS_ID);
        new BukkitReflection();

        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CommandServerUtils());
        this.commandPlugins = null;

        CommandCompletions<BukkitCommandCompletionContext> completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("plugins", context -> getPluginNames());
        completions.registerAsyncCompletion("pluginJars", context -> getPluginFileNames());
        completions.registerAsyncCompletion("supportedConfigs  ", context -> CommandServerUtils.getSupportedConfigs());
        completions.registerAsyncCompletion("commands", context -> {
            try {
                return RCommandMap.getKnownCommands(RCraftServer.getCommandMap()).keySet();
            } catch (IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
            return Collections.emptyList();
        });
        reload();

        Bukkit.getPluginManager().registerEvents(new MainListener(), this);

        new VersionManager();
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        restoreBukkitPluginCommand();
    }

    private List<String> getPluginNames() {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getName)
                .collect(Collectors.toList());
    }

    private List<String> getPluginFileNames() {
        return Arrays.stream(getJars())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    private void removeCommands(String... commands) {
        Map<String, Command> map;
        try {
            map = RCommandMap.getKnownCommands(RCraftServer.getCommandMap());
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        for (String command : commands) {
            map.remove(command);
        }
    }

    public void restoreBukkitPluginCommand() {
        RCraftServer.getCommandMap().register("bukkit", new PluginsCommand("plugins"));
    }

    /**
     * Reloads the configurations of the plugin.
     * Also makes sure the bukkit /pl command gets restored.
     */
    public void reload() {
        if (commandPlugins != null) {
            commandManager.unregisterCommand(commandPlugins);
            restoreBukkitPluginCommand();
        }

        new Config(copyResourceIfNotExists("config.yml", "bukkit-config.yml"));
        new Messenger(copyResourceIfNotExists("messages.yml", "bukkit-messages.yml"));

        if (!Config.getInstance().getBoolean("settings.disable-plugins-command")) {
            this.removeCommands("pl", "plugins");
            this.commandPlugins = new CommandPlugins();
            commandManager.registerCommand(commandPlugins);
        }
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Retrieves all files with a jar extension in the plugins/ folder.
     * @return An array of jar files.
     */
    public File[] getJars() {
        File parent = getDataFolder().getParentFile();
        if (parent == null) return new File[0];
        return parent.listFiles(f -> f.getName().endsWith(".jar"));
    }

    private void createDataFolderIfNotExists() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
    }

    private File copyResourceIfNotExists(String targetName, String resource) {
        createDataFolderIfNotExists();

        File file = new File(getDataFolder(), targetName);
        if (!file.exists()) {
            getLogger().info(String.format("'%s' not found, creating!", targetName));
            saveResource(resource, false);
            File copiedFile = new File(getDataFolder(), resource);
            copiedFile.renameTo(file);
        }
        return file;
    }

    private void checkForUpdates() {
        if (Config.getInstance().getBoolean("settings.check-updates")) {
            UpdateCheckerTask.start(Bukkit.getConsoleSender(), true);
        }
    }
}
