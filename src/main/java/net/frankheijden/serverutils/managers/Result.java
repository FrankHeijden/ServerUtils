package net.frankheijden.serverutils.managers;

import net.frankheijden.serverutils.config.Messenger;
import org.bukkit.command.CommandSender;

public enum Result {
    NOT_EXISTS,
    NOT_ENABLED,
    ALREADY_ENABLED,
    FILE_CHANGED,
    INVALID_DESCRIPTION,
    ERROR,
    SUCCESS;

    public void sendTo(CommandSender sender, String action, String what) {
        Messenger.sendMessage(sender, "serverutils." + this.name().toLowerCase(),
                "%action%", action,
                "%what%", what);
    }
}
