package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.md_5.bungee.api.CommandSender;

public class BungeeCommandSender implements ServerCommandSender {

    private final CommandSender sender;

    public BungeeCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }
}
