package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A wrap for a Bukkit CommandSender.
 */
public class BukkitCommandSender implements ServerCommandSender {

    private final CommandSender sender;

    /**
     * Constructs a new CommandSender instance.
     * @param sender The sender to wrap.
     */
    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    /**
     * Sends a message to a CommandSender.
     * @param message The message to send.
     */
    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    /**
     * Checks if the CommandSender has a permission.
     * @param permission The permission to check.
     * @return Whether or not they have the permission.
     */
    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    /**
     * Whether or not the given instance is a player.
     * @return Boolean true or false.
     */
    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }
}
