package net.frankheijden.serverutils.bungee.events;

import net.frankheijden.serverutils.common.events.PluginLoadEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePluginLoadEvent extends BungeePluginEvent implements PluginLoadEvent<Plugin> {

    public BungeePluginLoadEvent(Plugin plugin, Stage stage) {
        super(plugin, stage);
    }
}
