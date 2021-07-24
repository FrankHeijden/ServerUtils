package net.frankheijden.serverutils.bungee.listeners;

import net.frankheijden.serverutils.bungee.entities.BungeeCommandSender;
import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.common.listeners.ServerListener;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

public class BungeeServerListener extends ServerListener<
        BungeePlugin,
        Plugin,
        ScheduledTask,
        BungeeCommandSender,
        CommandSender
        > implements Listener {

    public BungeeServerListener(BungeePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) return;
        handleUpdate(plugin.getChatProvider().get(event.getPlayer()));
    }
}
