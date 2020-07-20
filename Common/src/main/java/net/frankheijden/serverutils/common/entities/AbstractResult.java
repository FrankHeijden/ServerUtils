package net.frankheijden.serverutils.common.entities;

public interface AbstractResult {

    /**
     * Retrieves the associated message of the result
     * and sends it to a CommandSender.
     * @param sender The receiver.
     * @param action The action which let to the result.
     * @param what An associated variable.
     */
    void sendTo(ServerCommandSender sender, String action, String what);
}
