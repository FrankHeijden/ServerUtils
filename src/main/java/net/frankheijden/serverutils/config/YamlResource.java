package net.frankheijden.serverutils.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.frankheijden.serverutils.ServerUtils;
import net.frankheijden.serverutils.utils.YamlUtils;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class YamlResource {

    private static final ServerUtils plugin = ServerUtils.getInstance();

    private final YamlConfiguration configuration;

    /**
     * Creates a new YamlResource instance.
     * Loads the resource from the jar file.
     * @param file The destination file.
     * @param resource The resource from the jar file.
     */
    public YamlResource(File file, String resource) {
        InputStream is = plugin.getResource(resource);
        YamlConfiguration def = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
        configuration = YamlUtils.init(file, def);
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }
}
