package net.frankheijden.serverutils.bungee.entities;

import java.io.File;
import java.util.logging.Logger;
import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.managers.BungeePluginManager;
import net.frankheijden.serverutils.bungee.managers.BungeeTaskManager;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class BungeePlugin extends ServerUtilsPlugin {

    private final ServerUtils plugin;
    private final BungeePluginManager pluginManager;
    private final BungeeTaskManager taskManager;
    private final BungeeResourceProvider resourceProvider;
    private final BungeeChatProvider chatProvider;

    /**
     * Creates a new BungeePlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public BungeePlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginManager = new BungeePluginManager();
        this.taskManager = new BungeeTaskManager();
        this.resourceProvider = new BungeeResourceProvider(plugin);
        this.chatProvider = new BungeeChatProvider();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BungeePluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BungeeTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public BungeeResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public BungeeChatProvider getChatProvider() {
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
    @SuppressWarnings("unchecked")
    public PluginDescription fetchUpdaterData() {
        try {
            return BungeePluginManager.getPluginDescription(pluginManager.getPluginFile("ServerUtils"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
