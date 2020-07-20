package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommandSender implements ServerCommandSender {

    private final CommandSender sender;

    public BungeeCommandSender(CommandSender sender) {
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

    /**
     * Whether or not the given instance is a player.
     * @return Boolean true or false.
     */
    @Override
    public boolean isPlayer() {
        return sender instanceof ProxiedPlayer;
    }
}
