package net.frankheijden.serverutils.bungee.events;

import net.frankheijden.serverutils.common.events.PluginUnloadEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePluginUnloadEvent extends BungeePluginEvent implements PluginUnloadEvent<Plugin> {

    public BungeePluginUnloadEvent(Plugin plugin, Stage stage) {
        super(plugin, stage);
    }
}
