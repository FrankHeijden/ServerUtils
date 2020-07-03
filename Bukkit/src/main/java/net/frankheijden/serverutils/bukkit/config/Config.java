package net.frankheijden.serverutils.bukkit.config;

import java.io.File;

public class Config extends YamlResource {

    private static Config instance;

    public Config(File file) {
        super(file, "bukkit-config.yml");
        instance = this;
    }

    public static Config getInstance() {
        return instance;
    }

    public boolean getBoolean(String path) {
        return getConfiguration().getBoolean(path);
    }
}
