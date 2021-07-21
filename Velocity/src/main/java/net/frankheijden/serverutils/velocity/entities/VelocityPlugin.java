package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.plugin.PluginDescription;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginManager;
import net.frankheijden.serverutils.velocity.managers.VelocityTaskManager;
import net.frankheijden.serverutils.velocity.reflection.RJavaPluginLoader;

public class VelocityPlugin extends ServerUtilsPlugin {

    private final ServerUtils plugin;
    private final VelocityPluginManager pluginManager;
    private final VelocityTaskManager taskManager;
    private final VelocityResourceProvider resourceProvider;
    private final VelocityChatProvider chatProvider;

    /**
     * Creates a new BungeePlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public VelocityPlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginManager = new VelocityPluginManager();
        this.taskManager = new VelocityTaskManager(plugin);
        this.resourceProvider = new VelocityResourceProvider(plugin);
        this.chatProvider = new VelocityChatProvider(plugin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VelocityPluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public VelocityTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public VelocityResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public VelocityChatProvider getChatProvider() {
        return chatProvider;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(plugin.getLogger().getName());
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataDirectory().toFile();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PluginDescription fetchUpdaterData() {
        Path pluginPath = pluginManager.getPluginFile("ServerUtils").toPath();
        Object javaPluginLoader = RJavaPluginLoader.newInstance(plugin.getProxy(), pluginPath.getParent());
        return RJavaPluginLoader.loadPluginDescription(javaPluginLoader, pluginPath);
    }
}
