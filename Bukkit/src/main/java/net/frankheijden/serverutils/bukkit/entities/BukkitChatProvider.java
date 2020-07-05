package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.bukkit.utils.BukkitUtils;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class BukkitChatProvider extends ChatProvider {

    @Override
    public ServerCommandSender getConsoleSender() {
        return BukkitUtils.wrap(Bukkit.getConsoleSender());
    }

    @Override
    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Override
    public void broadcast(String permission, String message) {
        Bukkit.broadcast(message, permission);
    }
}
