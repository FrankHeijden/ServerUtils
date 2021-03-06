package net.frankheijden.serverutils.common.entities;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import net.frankheijden.serverutils.common.managers.UpdateManager;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.common.utils.FileUtils;

public abstract class ServerUtilsPlugin {

    private final UpdateManager updateManager = new UpdateManager();

    public abstract <T> AbstractPluginManager<T> getPluginManager();

    public abstract <T> AbstractTaskManager<T> getTaskManager();

    public abstract ResourceProvider getResourceProvider();

    public abstract ChatProvider getChatProvider();

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public abstract Logger getLogger();

    public abstract File getDataFolder();

    public abstract <T> T fetchUpdaterData();

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

    public void enable() {

    }

    public void disable() {
        getTaskManager().cancelAllTasks();
    }
}
