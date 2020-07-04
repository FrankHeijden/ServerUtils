package net.frankheijden.serverutils.common.entities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.frankheijden.serverutils.common.providers.ColorProvider;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.common.utils.FileUtils;

public abstract class ServerUtilsPlugin {

    public abstract <T> PluginProvider<T> getPluginProvider();

    public abstract ResourceProvider getResourceProvider();

    public abstract ColorProvider getColorProvider();

    public abstract Logger getLogger();

    public abstract File getDataFolder();

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
            getLogger().info(String.format("'%s' not found, creating!", targetName));
            try {
                FileUtils.saveResource(getResourceProvider().getResource(resource), file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return file;
    }
}
