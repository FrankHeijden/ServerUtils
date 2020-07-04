package net.frankheijden.serverutils.bungee.entities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.common.config.YamlConfig;

public class BungeeResourceProvider implements ResourceProvider {

    private final ServerUtils plugin;

    public BungeeResourceProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public InputStream getResource(String resource) {
        return plugin.getResourceAsStream(resource);
    }

    @Override
    public YamlConfig load(InputStream is) {
        return new BungeeYamlConfig(is);
    }

    @Override
    public YamlConfig load(File file) {
        try {
            return new BungeeYamlConfig(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
