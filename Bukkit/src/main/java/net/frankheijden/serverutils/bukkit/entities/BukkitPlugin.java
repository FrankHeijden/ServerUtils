package net.frankheijden.serverutils.bukkit.entities;

import java.io.File;
import java.util.logging.Logger;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.managers.BukkitTaskManager;
import net.frankheijden.serverutils.bukkit.managers.BukkitVersionManager;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class BukkitPlugin extends ServerUtilsPlugin {

    private final ServerUtils plugin;
    private final BukkitPluginManager pluginManager;
    private final BukkitTaskManager taskManager;
    private final BukkitResourceProvider resourceProvider;
    private final BukkitChatProvider chatProvider;
    private final BukkitVersionManager versionManager;

    /**
     * Creates a new BukkitPlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public BukkitPlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginManager = new BukkitPluginManager(plugin);
        this.taskManager = new BukkitTaskManager();
        this.resourceProvider = new BukkitResourceProvider(plugin);
        this.chatProvider = new BukkitChatProvider();
        this.versionManager = new BukkitVersionManager(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BukkitPluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BukkitTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public BukkitResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public BukkitChatProvider getChatProvider() {
        return chatProvider;
    }

    @Override
    public BukkitVersionManager getVersionManager() {
        return versionManager;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
