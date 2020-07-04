package net.frankheijden.serverutils.bukkit.utils;

import net.frankheijden.serverutils.bukkit.entities.BukkitCommandSender;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import org.bukkit.command.CommandSender;

public class BukkitUtils {

    public static ServerCommandSender wrap(CommandSender sender) {
        return new BukkitCommandSender(sender);
    }
}
