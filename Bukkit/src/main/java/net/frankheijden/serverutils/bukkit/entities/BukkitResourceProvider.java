package net.frankheijden.serverutils.bukkit.entities;

import java.io.File;
import java.io.InputStream;
import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.providers.ResourceProvider;

public class BukkitResourceProvider implements ResourceProvider {

    private final ServerUtils plugin;

    public BukkitResourceProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public InputStream getResource(String resource) {
        return getRawResource(resource + getResourceExtension());
    }

    @Override
    public InputStream getRawResource(String resource) {
        return plugin.getResource(resource);
    }

    @Override
    public ServerUtilsConfig load(InputStream is) {
        return new BukkitYamlConfig(is);
    }

    @Override
    public ServerUtilsConfig load(File file) {
        return new BukkitYamlConfig(file);
    }

    @Override
    public String getResourceExtension() {
        return ".yml";
    }
}
