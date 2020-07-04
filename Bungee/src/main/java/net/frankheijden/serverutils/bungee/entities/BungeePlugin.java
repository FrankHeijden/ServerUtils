package net.frankheijden.serverutils.bungee.entities;

import java.io.File;
import java.util.logging.Logger;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.common.providers.ColorProvider;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends ServerUtilsPlugin {

    private final ServerUtils plugin;
    private final PluginProvider<Plugin> pluginProvider;
    private final ResourceProvider resourceProvider;
    private final ColorProvider colorProvider;

    /**
     * Creates a new BungeePlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public BungeePlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginProvider = new BungeePluginProvider(plugin);
        this.resourceProvider = new BungeeResourceProvider(plugin);
        this.colorProvider = new BungeeColorProvider();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PluginProvider<Plugin> getPluginProvider() {
        return pluginProvider;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public ColorProvider getColorProvider() {
        return colorProvider;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }
}
