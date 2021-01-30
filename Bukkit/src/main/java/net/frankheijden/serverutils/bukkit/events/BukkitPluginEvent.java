package net.frankheijden.serverutils.bukkit.events;

import net.frankheijden.serverutils.common.events.PluginEvent;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public abstract class BukkitPluginEvent extends Event implements PluginEvent<Plugin> {

    private final Plugin plugin;
    private final Stage stage;

    protected BukkitPluginEvent(Plugin plugin, Stage stage) {
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
