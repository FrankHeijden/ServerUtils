package net.frankheijden.serverutils.common.entities;

/**
 * A basic wrapper for a CommandSender.
 */
public interface ServerCommandSender {

    /**
     * Sends a message to a CommandSender.
     * @param message The message to send.
     */
    void sendMessage(String message);

    /**
     * Checks if the CommandSender has a permission.
     * @param permission The permission to check.
     * @return Whether or not they have the permission.
     */
    boolean hasPermission(String permission);

    /**
     * Whether or not the given instance is a player.
     * @return Boolean true or false.
     */
    boolean isPlayer();
}
