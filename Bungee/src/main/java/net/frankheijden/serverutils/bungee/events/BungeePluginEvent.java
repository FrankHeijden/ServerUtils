package net.frankheijden.serverutils.bungee.events;

import net.frankheijden.serverutils.common.events.PluginEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class BungeePluginEvent extends Event implements PluginEvent<Plugin> {

    private final Plugin plugin;
    private final Stage stage;

    protected BungeePluginEvent(Plugin plugin, Stage stage) {
        this.plugin = plugin;
        this.stage = stage;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
