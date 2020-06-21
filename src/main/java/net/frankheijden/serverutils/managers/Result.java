package net.frankheijden.serverutils.managers;

import net.frankheijden.serverutils.config.Messenger;
import org.bukkit.command.CommandSender;

public enum Result {
    NOT_EXISTS,
    NOT_ENABLED,
    ALREADY_LOADED,
    ALREADY_ENABLED,
    ALREADY_DISABLED,
    FILE_CHANGED,
    INVALID_DESCRIPTION,
    INVALID_PLUGIN,
    UNKNOWN_DEPENDENCY,
    ERROR,
    SUCCESS;

    private String arg;

    Result() {
        this.arg = "";
    }

    public Result arg(String arg) {
        this.arg = arg;
        return this;
    }

    public void sendTo(CommandSender sender, String action, String what) {
        Messenger.sendMessage(sender, "serverutils." + this.name().toLowerCase(),
                "%action%", action,
                "%what%", what,
                "%arg%", arg);
    }
}
