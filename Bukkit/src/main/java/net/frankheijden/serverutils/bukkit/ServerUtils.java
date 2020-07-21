package net.frankheijden.serverutils.bukkit;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;

import java.util.Map;

import net.frankheijden.serverutils.bukkit.commands.CommandPlugins;
import net.frankheijden.serverutils.bukkit.commands.CommandServerUtils;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.bukkit.entities.BukkitReflection;
import net.frankheijden.serverutils.bukkit.listeners.BukkitListener;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.reflection.RCommandMap;
import net.frankheijden.serverutils.bukkit.reflection.RCraftServer;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.config.Messenger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerUtils extends JavaPlugin implements CommandExecutor {

    private static ServerUtils instance;
    private static final String CONFIG_RESOURCE = "bukkit-config.yml";
    private static final String MESSAGES_RESOURCE = "bukkit-messages.yml";

    private BukkitPlugin plugin;
    private PaperCommandManager commandManager;
    private CommandPlugins commandPlugins;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        this.plugin = new BukkitPlugin(this);
        ServerUtilsApp.init(this, plugin);

        new Metrics(this, ServerUtilsApp.BSTATS_METRICS_ID);
        new BukkitReflection();

        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CommandServerUtils());
        this.commandPlugins = null;

        BukkitPluginManager manager = plugin.getPluginManager();
        CommandCompletions<BukkitCommandCompletionContext> completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("plugins", context -> manager.getPluginNames());
        completions.registerAsyncCompletion("pluginJars", context -> manager.getPluginFileNames());
        completions.registerAsyncCompletion("supportedConfigs", context -> CommandServerUtils.getSupportedConfigs());
        completions.registerAsyncCompletion("commands", context -> manager.getCommands());
        reload();

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        plugin.enable();

        ServerUtilsApp.tryCheckForUpdates();
    }

    public static ServerUtils getInstance() {
        return instance;
    }

    public BukkitPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        restoreBukkitPluginCommand();
        commandManager.unregisterCommands();
        plugin.disable();
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

        new Config("config.yml", CONFIG_RESOURCE);
        new Messenger("messages.yml", MESSAGES_RESOURCE);

        if (!Config.getInstance().getConfig().getBoolean("settings.disable-plugins-command")) {
            this.removeCommands("pl", "plugins");
            this.commandPlugins = new CommandPlugins();
            commandManager.registerCommand(commandPlugins);
        }
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }
}
