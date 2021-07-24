package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.utils.HexUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BukkitChatProvider implements ChatProvider<BukkitCommandSender, CommandSender> {

    @Override
    public BukkitCommandSender getConsoleSender() {
        return new BukkitCommandSender(Bukkit.getConsoleSender());
    }

    @Override
    public BukkitCommandSender get(CommandSender source) {
        return new BukkitCommandSender(source);
    }

    @Override
    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', HexUtils.convertHexString(str));
    }

    @Override
    public void broadcast(String permission, String message) {
        Bukkit.broadcast(message, permission);
    }
}
