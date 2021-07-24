package net.frankheijden.serverutils.bungee;

import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

public class ServerUtils extends Plugin {

    private static ServerUtils instance;

    private BungeePlugin plugin;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        this.plugin = new BungeePlugin(this);
        ServerUtilsApp.init(this, plugin);

        new Metrics(this, ServerUtilsApp.BSTATS_METRICS_ID);
        plugin.enable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        plugin.disable();
    }

    public static ServerUtils getInstance() {
        return instance;
    }

    public BungeePlugin getPlugin() {
        return plugin;
    }
}
