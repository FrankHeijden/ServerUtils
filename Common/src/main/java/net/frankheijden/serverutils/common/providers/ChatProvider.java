package net.frankheijden.serverutils.common.providers;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;

/**
 * A basic chat provider class.
 */
public abstract class ChatProvider {

    /**
     * Retrieves the console sender of a server instance.
     * @return The console sender.
     */
    public abstract ServerCommandSender getConsoleSender();

    /**
     * Colorizes the given string.
     * @param str The string to color.
     * @return The colored string.
     */
    public abstract String color(String str);

    /**
     * Broadcasts a message over a server instance.
     * @param permission The permission the receivers need to have.
     * @param message The message to broadcast.
     */
    public abstract void broadcast(String permission, String message);
}
