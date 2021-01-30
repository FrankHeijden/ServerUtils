package net.frankheijden.serverutils.bukkit.events;

import net.frankheijden.serverutils.common.events.PluginDisableEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class BukkitPluginDisableEvent extends BukkitPluginEvent implements PluginDisableEvent<Plugin> {

    private static final HandlerList handlers = new HandlerList();

    public BukkitPluginDisableEvent(Plugin plugin, Stage stage) {
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
