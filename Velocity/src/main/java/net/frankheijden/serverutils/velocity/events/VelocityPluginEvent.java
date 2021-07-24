package net.frankheijden.serverutils.velocity.events;

import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.events.PluginEvent;

public abstract class VelocityPluginEvent implements PluginEvent<PluginContainer> {

    private final PluginContainer plugin;
    private final Stage stage;

    protected VelocityPluginEvent(PluginContainer plugin, Stage stage) {
        this.plugin = plugin;
        this.stage = stage;
    }

    @Override
    public PluginContainer getPlugin() {
        return plugin;
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
