package net.frankheijden.serverutils.bungee.events;

import net.frankheijden.serverutils.common.events.PluginDisableEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePluginDisableEvent extends BungeePluginEvent implements PluginDisableEvent<Plugin> {

    public BungeePluginDisableEvent(Plugin plugin, Stage stage) {
        super(plugin, stage);
    }
}
