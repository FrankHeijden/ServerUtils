package net.frankheijden.serverutils.bungee.entities;

import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import java.io.File;
import java.util.logging.Logger;
import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.commands.BungeeCommandPlugins;
import net.frankheijden.serverutils.bungee.commands.BungeeCommandServerUtils;
import net.frankheijden.serverutils.bungee.listeners.BungeePlayerListener;
import net.frankheijden.serverutils.bungee.managers.BungeePluginManager;
import net.frankheijden.serverutils.bungee.managers.BungeeTaskManager;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeePlugin extends ServerUtilsPlugin<Plugin, ScheduledTask, BungeeAudience, CommandSender, BungeePluginDescription> {

    private final ServerUtils plugin;
    private final BungeePluginManager pluginManager;
    private final BungeeTaskManager taskManager;
    private final BungeeResourceProvider resourceProvider;
    private final BungeeAudiences audiences;
    private final BungeeAudienceProvider chatProvider;

    /**
     * Creates a new BungeePlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public BungeePlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginManager = new BungeePluginManager();
        this.taskManager = new BungeeTaskManager();
        this.resourceProvider = new BungeeResourceProvider(plugin);
        this.audiences = BungeeAudiences.create(plugin);
        this.chatProvider = new BungeeAudienceProvider(plugin, audiences);
    }

    @Override
    protected BungeeCommandManager<BungeeAudience> newCommandManager() {
        return new BungeeCommandManager<>(
                plugin,
                AsynchronousCommandExecutionCoordinator.<BungeeAudience>newBuilder().build(),
                chatProvider::get,
                BungeeAudience::getSource
        );
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUNGEE;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public BungeePluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public BungeeTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public BungeeResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public BungeeAudienceProvider getChatProvider() {
        return chatProvider;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    protected void enablePlugin() {
        plugin.getProxy().getPluginManager().registerListener(plugin, new BungeePlayerListener(this));
    }

    @Override
    protected void disablePlugin() {
        this.audiences.close();
    }

    @Override
    protected void reloadPlugin() {
        new BungeeCommandPlugins(this).register(commandManager);
        new BungeeCommandServerUtils(this).register(commandManager);
    }
}
