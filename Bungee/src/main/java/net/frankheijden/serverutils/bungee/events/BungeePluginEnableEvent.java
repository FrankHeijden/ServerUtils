package net.frankheijden.serverutils.bungee.events;

import net.frankheijden.serverutils.common.events.PluginEnableEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePluginEnableEvent extends BungeePluginEvent implements PluginEnableEvent<Plugin> {

    public BungeePluginEnableEvent(Plugin plugin, Stage stage) {
        super(plugin, stage);
    }
}
