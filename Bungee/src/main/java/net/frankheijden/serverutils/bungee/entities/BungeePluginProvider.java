package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.managers.PluginManager;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BungeePluginProvider extends PluginProvider<Plugin> {

    private final ServerUtils plugin;

    public BungeePluginProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getPluginsFolder() {
        return plugin.getProxy().getPluginsFolder();
    }

    @Override
    public List<Plugin> getPlugins() {
        return getPlugins(false);
    }

    public List<Plugin> getPlugins(boolean modules) {
        Collection<Plugin> plugins = plugin.getProxy().getPluginManager().getPlugins();
        if (modules) return new ArrayList<>(plugins);
        return plugins.stream()
                .filter(PluginManager::isPlugin)
                .collect(Collectors.toList());
    }

    @Override
    public String getPluginName(Plugin plugin) {
        return plugin.getDataFolder().getName();
    }

    public List<Plugin> getPluginsSorted(boolean modules) {
        List<Plugin> plugins = getPlugins(modules);
        plugins.sort(Comparator.comparing(this::getPluginName));
        return plugins;
    }
}
