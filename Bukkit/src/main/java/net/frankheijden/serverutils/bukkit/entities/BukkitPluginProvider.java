package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BukkitPluginProvider extends PluginProvider<Plugin> {

    private final ServerUtils plugin;

    public BukkitPluginProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getPluginsFolder() {
        return plugin.getDataFolder().getParentFile();
    }

    @Override
    public List<Plugin> getPlugins() {
        return Arrays.asList(Bukkit.getPluginManager().getPlugins());
    }

    @Override
    public String getPluginName(Plugin plugin) {
        return plugin.getName();
    }
}
