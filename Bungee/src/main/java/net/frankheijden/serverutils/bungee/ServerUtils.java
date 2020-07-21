package net.frankheijden.serverutils.bungee;

import co.aikar.commands.BungeeCommandCompletionContext;
import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.CommandCompletions;
import net.frankheijden.serverutils.bungee.commands.CommandPlugins;
import net.frankheijden.serverutils.bungee.commands.CommandServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.bungee.entities.BungeeReflection;
import net.frankheijden.serverutils.bungee.listeners.BungeeListener;
import net.frankheijden.serverutils.bungee.managers.BungeePluginManager;
import net.frankheijden.serverutils.bungee.reflection.RPluginClassLoader;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.utils.MapUtils;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

public class ServerUtils extends Plugin {

    private static ServerUtils instance;
    private static final String CONFIG_RESOURCE = "bungee-config.yml";
    private static final String MESSAGES_RESOURCE = "bungee-messages.yml";

    private BungeePlugin plugin;
    private BungeeCommandManager commandManager;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        this.plugin = new BungeePlugin(this);
        ServerUtilsApp.init(this, plugin);

        new Metrics(this, ServerUtilsApp.BSTATS_METRICS_ID);
        new BungeeReflection();

        this.commandManager = new BungeeCommandManager(this);
        commandManager.registerCommand(new CommandPlugins());
        commandManager.registerCommand(new CommandServerUtils());

        BungeePluginManager manager = plugin.getPluginManager();
        CommandCompletions<BungeeCommandCompletionContext> commandCompletions = commandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("plugins", context -> manager.getPluginNames());
        commandCompletions.registerAsyncCompletion("pluginJars", context -> manager.getPluginFileNames());
        commandCompletions.registerAsyncCompletion("commands", context -> manager.getCommands());

        reload();
        getProxy().getPluginManager().registerListener(this, new BungeeListener());

        plugin.enable();

        loadClasses();
        ServerUtilsApp.tryCheckForUpdates();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        commandManager.unregisterCommands();
        plugin.disable();
    }

    /**
     * Loads some classes in memory so they're available during updating.
     */
    private void loadClasses() {
        new RPluginManager();
        new MapUtils();
        new CloseableResult(Result.SUCCESS);
        new RPluginClassLoader();
    }

    public static ServerUtils getInstance() {
        return instance;
    }

    public BungeePlugin getPlugin() {
        return plugin;
    }

    public BungeeCommandManager getCommandManager() {
        return commandManager;
    }

    public void reload() {
        new Config("config.yml", CONFIG_RESOURCE);
        new Messenger("messages.yml", MESSAGES_RESOURCE);
    }
}
