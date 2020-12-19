package net.frankheijden.serverutils.bukkit.entities;

import java.io.File;
import java.io.InputStream;
import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.config.YamlConfig;
import net.frankheijden.serverutils.common.providers.ResourceProvider;

public class BukkitResourceProvider implements ResourceProvider {

    private final ServerUtils plugin;

    public BukkitResourceProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public InputStream getResource(String resource) {
        return plugin.getResource(resource);
    }

    @Override
    public YamlConfig load(InputStream is) {
        return new BukkitYamlConfig(is);
    }

    @Override
    public YamlConfig load(File file) {
        return new BukkitYamlConfig(file);
    }
}
