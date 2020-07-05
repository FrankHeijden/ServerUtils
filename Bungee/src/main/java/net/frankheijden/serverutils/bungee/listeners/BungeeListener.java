package net.frankheijden.serverutils.bungee.listeners;

import net.frankheijden.serverutils.bungee.utils.BungeeUtils;
import net.frankheijden.serverutils.common.listeners.ServerListener;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) return;
        ServerListener.handleUpdate(BungeeUtils.wrap(event.getPlayer()));
    }
}
