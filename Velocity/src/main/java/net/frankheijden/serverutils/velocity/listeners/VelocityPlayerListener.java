package net.frankheijden.serverutils.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.listeners.PlayerListener;
import net.frankheijden.serverutils.velocity.entities.VelocityCommandSender;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;

public class VelocityPlayerListener
        extends PlayerListener<VelocityPlugin, PluginContainer, VelocityCommandSender> {

    public VelocityPlayerListener(VelocityPlugin plugin) {
        super(plugin);
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        handleUpdate(plugin.getChatProvider().get(event.getPlayer()));
    }
}
