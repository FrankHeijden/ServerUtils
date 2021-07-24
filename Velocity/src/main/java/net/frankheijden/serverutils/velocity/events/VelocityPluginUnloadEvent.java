package net.frankheijden.serverutils.velocity.events;

import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.events.PluginUnloadEvent;

public class VelocityPluginUnloadEvent extends VelocityPluginEvent implements PluginUnloadEvent<PluginContainer> {

    public VelocityPluginUnloadEvent(PluginContainer plugin, Stage stage) {
        super(plugin, stage);
    }
}
