package net.frankheijden.serverutils.bukkit.events;

import net.frankheijden.serverutils.common.events.PluginUnloadEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class BukkitPluginUnloadEvent extends BukkitPluginEvent implements PluginUnloadEvent<Plugin> {

    private static final HandlerList handlers = new HandlerList();

    public BukkitPluginUnloadEvent(Plugin plugin, Stage stage) {
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
