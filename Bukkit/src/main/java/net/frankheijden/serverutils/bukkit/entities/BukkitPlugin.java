package net.frankheijden.serverutils.bukkit.entities;

import java.io.File;
import java.util.logging.Logger;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.providers.ColorProvider;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import net.frankheijden.serverutils.common.providers.ResourceProvider;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import org.bukkit.plugin.Plugin;

public class BukkitPlugin extends ServerUtilsPlugin {

    private final ServerUtils plugin;
    private final PluginProvider<Plugin> pluginProvider;
    private final ResourceProvider resourceProvider;
    private final ColorProvider colorProvider;

    /**
     * Creates a new BukkitPlugin instance of ServerUtils.
     * @param plugin The ServerUtils plugin.
     */
    public BukkitPlugin(ServerUtils plugin) {
        this.plugin = plugin;
        this.pluginProvider = new BukkitPluginProvider(plugin);
        this.resourceProvider = new BukkitResourceProvider(plugin);
        this.colorProvider = new BukkitColorProvider();
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
