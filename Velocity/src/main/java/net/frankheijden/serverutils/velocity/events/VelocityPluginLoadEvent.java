package net.frankheijden.serverutils.velocity.events;

import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.events.PluginLoadEvent;

public class VelocityPluginLoadEvent extends VelocityPluginEvent implements PluginLoadEvent<PluginContainer> {

    public VelocityPluginLoadEvent(PluginContainer plugin, Stage stage) {
        super(plugin, stage);
    }
}
