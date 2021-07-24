package net.frankheijden.serverutils.bukkit.entities;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import java.io.File;
import java.util.logging.Logger;
import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.bukkit.commands.BukkitCommandPlugins;
import net.frankheijden.serverutils.bukkit.commands.BukkitCommandServerUtils;
import net.frankheijden.serverutils.bukkit.listeners.BukkitListener;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.managers.BukkitTaskManager;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitPlugin extends ServerUtilsPlugin<
        Plugin,
        BukkitTask,
        BukkitCommandSender,
        CommandSender
        > {

    private final ServerUtils plugin;
    private final BukkitPluginManager pluginManager;
    private final BukkitTaskManager taskManager;
    private final BukkitResourceProvider resourceProvider;
    private final BukkitChatProvider chatProvider;
    private boolean registeredPluginsCommand;

    /**
     * Creates a new BukkitPlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public BukkitPlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginManager = new BukkitPluginManager();
        this.taskManager = new BukkitTaskManager();
        this.resourceProvider = new BukkitResourceProvider(plugin);
        this.chatProvider = new BukkitChatProvider();
        this.registeredPluginsCommand = false;
    }

    @Override
    protected PaperCommandManager<BukkitCommandSender> newCommandManager() {
        try {
            return new PaperCommandManager<>(
                    plugin,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    chatProvider::get,
                    BukkitCommandSender::getSource
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

    @Override
    public BukkitPluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
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
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    protected void enablePlugin() {
        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), plugin);
    }

    @Override
    protected void reloadPlugin() {
        if (getConfigResource().getConfig().getBoolean("settings.disable-plugins-command")) {
            if (registeredPluginsCommand) {
                plugin.restoreBukkitPluginCommand();
                this.registeredPluginsCommand = false;
            }
        } else {
            new BukkitCommandPlugins(this).register(commandManager);
            this.registeredPluginsCommand = true;
        }
        new BukkitCommandServerUtils(this).register(commandManager);

        taskManager.runTask(() -> BukkitPluginManager.unregisterExactCommands(plugin.getDisabledCommands()));
    }
}
