package net.frankheijden.serverutils.bukkit.entities;

import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;
import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.bukkit.commands.BukkitCommandPlugins;
import net.frankheijden.serverutils.bukkit.commands.BukkitCommandServerUtils;
import net.frankheijden.serverutils.bukkit.config.BukkitMessageKey;
import net.frankheijden.serverutils.bukkit.listeners.BukkitPlayerListener;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.managers.BukkitTaskManager;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitPlugin extends ServerUtilsPlugin<Plugin, BukkitTask, BukkitAudience, CommandSender, BukkitPluginDescription> {

    private final ServerUtils plugin;
    private final BukkitPluginManager pluginManager;
    private final BukkitTaskManager taskManager;
    private final BukkitResourceProvider resourceProvider;
    private final BukkitAudiences audiences;
    private final BukkitAudienceProvider chatProvider;
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
        this.audiences = BukkitAudiences.create(plugin);
        this.chatProvider = new BukkitAudienceProvider(plugin, this.audiences);
        this.registeredPluginsCommand = false;
    }

    @Override
    protected PaperCommandManager<BukkitAudience> newCommandManager() {
        PaperCommandManager<BukkitAudience> commandManager;
        try {
            commandManager = new PaperCommandManager<>(
                    plugin,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    chatProvider::get,
                    BukkitAudience::getSource
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier();
            handleBrigadier(commandManager.brigadierManager());
        }

        if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        return commandManager;
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
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
    public BukkitAudienceProvider getChatProvider() {
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
        Bukkit.getPluginManager().registerEvents(new BukkitPlayerListener(this), plugin);
    }

    @Override
    protected void disablePlugin() {
        this.audiences.close();
    }

    @Override
    protected void reloadPlugin() {
        this.messagesResource.load(Arrays.asList(BukkitMessageKey.values()));
        if (getConfigResource().getConfig().getBoolean("settings.disable-plugins-command")) {
            if (registeredPluginsCommand) {
                BukkitPluginManager.unregisterCommands("pl", "plugins");
                plugin.restoreBukkitPluginCommand();
                this.registeredPluginsCommand = false;
            }
        } else {
            BukkitPluginManager.unregisterCommands("pl", "plugins");
            new BukkitCommandPlugins(this).register(commandManager);
            this.registeredPluginsCommand = true;
        }
        new BukkitCommandServerUtils(this).register(commandManager);

        taskManager.runTask(() -> BukkitPluginManager.unregisterExactCommands(plugin.getDisabledCommands()));
    }
}
