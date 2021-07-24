package net.frankheijden.serverutils.common.entities;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.CommandsResource;
import net.frankheijden.serverutils.common.config.ConfigResource;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import net.frankheijden.serverutils.common.managers.UpdateManager;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.common.utils.FileUtils;

public abstract class ServerUtilsPlugin<P, T, C extends ServerCommandSender<S>, S> {

    private final UpdateManager updateManager = new UpdateManager();
    private CommandsResource commandsResource;
    private ConfigResource configResource;
    private MessagesResource messagesResource;
    protected CommandManager<C> commandManager;

    public abstract Platform getPlatform();

    public abstract P getPlugin();

    public CommandsResource getCommandsResource() {
        return commandsResource;
    }

    public ConfigResource getConfigResource() {
        return configResource;
    }

    public MessagesResource getMessagesResource() {
        return messagesResource;
    }

    public abstract AbstractPluginManager<P> getPluginManager();

    public abstract AbstractTaskManager<T> getTaskManager();

    public abstract ResourceProvider getResourceProvider();

    public abstract ChatProvider<C, S> getChatProvider();

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public abstract Logger getLogger();

    public abstract File getDataFolder();

    public Collection<Command<C>> getCommands() {
        return commandManager.getCommands();
    }

    public void createDataFolderIfNotExists() {
        if (getDataFolder().exists()) return;
        getDataFolder().mkdirs();
    }

    /**
     * Copies a resource from the jar to the specified target file name under the datafolder.
     * @param targetName The target file under the datafolder.
     * @param resource The resource from the jar file to copy.
     * @return The target file.
     */
    public File copyResourceIfNotExists(String targetName, String resource) {
        createDataFolderIfNotExists();

        File file = new File(getDataFolder(), targetName);
        if (!file.exists()) {
            getLogger().info("'" + targetName + "' not found, creating!");
            try {
                FileUtils.saveResource(getResourceProvider().getResource(resource), file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return file;
    }

    protected abstract CommandManager<C> newCommandManager();

    /**
     * Enables the plugin.
     */
    public final void enable() {
        Path dataFolder = getDataFolder().toPath();
        if (Files.notExists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        reload();
        enablePlugin();
        ServerUtilsApp.tryCheckForUpdates();
        ServerUtilsApp.unloadServerUtilsUpdater();
    }

    protected void enablePlugin() {

    }

    public final void disable() {
        disablePlugin();
        getTaskManager().cancelAllTasks();
    }

    protected void disablePlugin() {

    }

    /**
     * Reloads the plugin's configurations.
     */
    public final void reload() {
        this.commandsResource = new CommandsResource(this);
        this.configResource = new ConfigResource(this);
        this.messagesResource = new MessagesResource(this);
        this.commandManager = newCommandManager();
        reloadPlugin();
    }

    protected void reloadPlugin() {

    }

    public enum Platform {
        BUKKIT,
        BUNGEE,
        VELOCITY,
    }
}
