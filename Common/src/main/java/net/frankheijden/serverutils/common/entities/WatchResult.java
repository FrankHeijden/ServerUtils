package net.frankheijden.serverutils.common.entities;

import net.frankheijden.serverutils.common.config.Messenger;

public enum WatchResult implements AbstractResult {

    START,
    CHANGE,
    NOT_WATCHING,
    STOPPED;

    /**
     * Retrieves the associated message of the result
     * and sends it to a CommandSender.
     * @param sender The receiver.
     * @param action The action which let to the result.
     * @param what An associated variable.
     */
    @Override
    public void sendTo(ServerCommandSender sender, String action, String what) {
        Messenger.sendMessage(sender, "serverutils.watcher." + this.name().toLowerCase(),
                "%what%", what);
    }
}
