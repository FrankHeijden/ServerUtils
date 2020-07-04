package net.frankheijden.serverutils.bungee.utils;

import net.frankheijden.serverutils.bungee.entities.BungeeCommandSender;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.md_5.bungee.api.CommandSender;

public class BungeeUtils {

    public static ServerCommandSender wrap(CommandSender sender) {
        return new BungeeCommandSender(sender);
    }
}
