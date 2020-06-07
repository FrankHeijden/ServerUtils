package net.frankheijden.serverutils.config;

import net.frankheijden.serverutils.ServerUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private static Defaults DEFAULT_CONFIG = Defaults.of(
            "settings", Defaults.of(
                    "disable-plugins-command", false,
                    "check-updates", true,
                    "download-updates", false,
                    "download-at-startup-and-update", false
            )
    );

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static Config instance;
    private final YamlConfiguration config;

    public Config(File file) {
        instance = this;
        config = Defaults.init(file, DEFAULT_CONFIG);
    }

    public static Config getInstance() {
        return instance;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path, (boolean) DEFAULT_CONFIG.get(path));
    }
}
