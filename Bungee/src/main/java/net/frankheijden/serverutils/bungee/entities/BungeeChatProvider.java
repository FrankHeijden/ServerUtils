package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.utils.HexUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class BungeeChatProvider implements ChatProvider<BungeeCommandSender, CommandSender> {

    @Override
    public BungeeCommandSender getConsoleSender() {
        return new BungeeCommandSender(ProxyServer.getInstance().getConsole());
    }

    @Override
    public BungeeCommandSender get(CommandSender source) {
        return new BungeeCommandSender(source);
    }

    @Override
    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', HexUtils.convertHexString(str));
    }

    @Override
    public void broadcast(String permission, String message) {
        ProxyServer.getInstance().getPlayers().stream()
                .filter(p -> p.hasPermission(permission))
                .forEach(p -> p.sendMessage(message));
    }
}
