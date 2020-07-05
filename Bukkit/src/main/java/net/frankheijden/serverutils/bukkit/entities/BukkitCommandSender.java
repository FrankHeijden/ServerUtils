package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import org.bukkit.command.CommandSender;

public class BukkitCommandSender implements ServerCommandSender {

    private final CommandSender sender;

    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }
}
