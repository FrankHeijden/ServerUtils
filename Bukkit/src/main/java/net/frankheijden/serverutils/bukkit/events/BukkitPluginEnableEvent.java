package net.frankheijden.serverutils.bukkit.events;

import net.frankheijden.serverutils.common.events.PluginEnableEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class BukkitPluginEnableEvent extends BukkitPluginEvent implements PluginEnableEvent<Plugin> {

    private static final HandlerList handlers = new HandlerList();

    public BukkitPluginEnableEvent(Plugin plugin, Stage stage) {
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
