package net.frankheijden.serverutils.velocity.entities;

import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.velocity.VelocityCommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import java.io.File;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.frankheijden.serverutils.velocity.commands.VelocityCommandPlugins;
import net.frankheijden.serverutils.velocity.commands.VelocityCommandServerUtils;
import net.frankheijden.serverutils.velocity.listeners.VelocityPlayerListener;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginManager;
import net.frankheijden.serverutils.velocity.managers.VelocityTaskManager;

public class VelocityPlugin extends ServerUtilsPlugin<
        PluginContainer,
        ScheduledTask,
        VelocityCommandSender,
        CommandSource
        > {

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
    protected VelocityCommandManager<VelocityCommandSender> newCommandManager() {
        return new VelocityCommandManager<>(
                plugin.getPluginContainer(),
                plugin.getProxy(),
                AsynchronousCommandExecutionCoordinator.<VelocityCommandSender>newBuilder().build(),
                chatProvider::get,
                VelocityCommandSender::getSource
        );
    }

    @Override
    public VelocityPluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public VelocityTaskManager getTaskManager() {
        return this.taskManager;
    }

    @Override
    public Platform getPlatform() {
        return Platform.VELOCITY;
    }

    @Override
    public VelocityResourceProvider getResourceProvider() {
        return this.resourceProvider;
    }

    @Override
    public VelocityChatProvider getChatProvider() {
        return this.chatProvider;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(plugin.getLogger().getName());
    }

    @Override
    public File getDataFolder() {
        return this.plugin.getDataDirectory().toFile();
    }

    @Override
    protected void enablePlugin() {
        plugin.getProxy().getEventManager().register(plugin, new VelocityPlayerListener(this));
    }

    @Override
    protected void reloadPlugin() {
        new VelocityCommandPlugins(this).register(commandManager);
        new VelocityCommandServerUtils(this).register(commandManager);
    }
}
