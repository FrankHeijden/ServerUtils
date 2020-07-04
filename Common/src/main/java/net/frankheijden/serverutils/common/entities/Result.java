package net.frankheijden.serverutils.common.entities;

import net.frankheijden.serverutils.common.config.Messenger;

public enum Result {
    NOT_EXISTS,
    NOT_ENABLED,
    ALREADY_LOADED,
    ALREADY_ENABLED,
    ALREADY_DISABLED,
    FILE_DELETED,
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

    /**
     * Retrieves the associated message of the result
     * and sends it to a CommandSender.
     * @param sender The receiver.
     * @param action The action which let to the result.
     * @param what An associated variable.
     */
    public void sendTo(ServerCommandSender sender, String action, String what) {
        Messenger.sendMessage(sender, "serverutils." + this.name().toLowerCase(),
                "%action%", action,
                "%what%", what,
                "%arg%", arg);
    }
}
