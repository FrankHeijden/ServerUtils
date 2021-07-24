package net.frankheijden.serverutils.velocity.events;

import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.events.PluginEnableEvent;

public class VelocityPluginEnableEvent extends VelocityPluginEvent implements PluginEnableEvent<PluginContainer> {

    public VelocityPluginEnableEvent(PluginContainer plugin, Stage stage) {
        super(plugin, stage);
    }
}
