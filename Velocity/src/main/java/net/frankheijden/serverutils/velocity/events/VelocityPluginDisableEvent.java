package net.frankheijden.serverutils.velocity.events;

import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.events.PluginDisableEvent;

public class VelocityPluginDisableEvent extends VelocityPluginEvent implements PluginDisableEvent<PluginContainer> {

    public VelocityPluginDisableEvent(PluginContainer plugin, Stage stage) {
        super(plugin, stage);
    }
}
