package net.frankheijden.serverutils.common.config;

import java.io.File;
import java.io.InputStream;

import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.providers.ResourceProvider;

public class YamlResource {

    private static final ServerUtilsPlugin plugin = ServerUtilsApp.getPlugin();

    private final YamlConfig config;

    /**
     * Creates a new YamlResource instance.
     * Loads the resource from the jar file.
     * @param fileName The destination file.
     * @param resource The resource from the jar file.
     */
    public YamlResource(String fileName, String resource) {
        ResourceProvider provider = plugin.getResourceProvider();
        InputStream is = provider.getResource(resource);
        File file = plugin.copyResourceIfNotExists(fileName, resource);
        config = YamlConfig.init(provider.load(is), provider.load(file));
    }

    public YamlConfig getConfig() {
        return config;
    }
}
