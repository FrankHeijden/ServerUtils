package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.bukkit.utils.BukkitUtils;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.utils.HexUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

/**
 * Provides basic chat functionality for Bukkit servers.
 */
public class BukkitChatProvider extends ChatProvider {

    /**
     * Retrieves the console sender of a Bukkit instance.
     * @return The console sender.
     */
    @Override
    public ServerCommandSender getConsoleSender() {
        return BukkitUtils.wrap(Bukkit.getConsoleSender());
    }

    /**
     * Colorizes the given string.
     * @param str The string to color.
     * @return The colored string.
     */
    @Override
    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', HexUtils.convertHexString(str));
    }

    /**
     * Broadcasts a message over a Bukkit instance.
     * @param permission The permission the receivers need to have.
     * @param message The message to broadcast.
     */
    @Override
    public void broadcast(String permission, String message) {
        Bukkit.broadcast(message, permission);
    }
}
