package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.bungee.utils.BungeeUtils;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

public class BungeeChatProvider extends ChatProvider {

    @Override
    public ServerCommandSender getConsoleSender() {
        return BungeeUtils.wrap(ProxyServer.getInstance().getConsole());
    }

    @Override
    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Override
    public void broadcast(String permission, String message) {
        ProxyServer.getInstance().getPlayers().stream()
                .filter(p -> p.hasPermission(permission))
                .forEach(p -> p.sendMessage(message));
    }
}
