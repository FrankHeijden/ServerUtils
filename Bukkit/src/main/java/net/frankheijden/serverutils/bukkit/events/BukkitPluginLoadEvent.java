package net.frankheijden.serverutils.bukkit.events;

import net.frankheijden.serverutils.common.events.PluginLoadEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class BukkitPluginLoadEvent extends BukkitPluginEvent implements PluginLoadEvent<Plugin> {

    private static final HandlerList handlers = new HandlerList();

    public BukkitPluginLoadEvent(Plugin plugin, Stage stage) {
        super(plugin, stage);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
