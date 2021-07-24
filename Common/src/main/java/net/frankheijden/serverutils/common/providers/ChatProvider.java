package net.frankheijden.serverutils.common.providers;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;

/**
 * A basic chat provider class.
 */
public interface ChatProvider<C extends ServerCommandSender<T>, T> {

    /**
     * Retrieves the console sender of a server instance.
     * @return The console sender.
     */
    C getConsoleSender();

    /**
     * Converts the given source (specific to impl) to a ServerCommandSender.
     */
    C get(T source);

    /**
     * Colorizes the given string.
     * @param str The string to color.
     * @return The colored string.
     */
    String color(String str);

    /**
     * Broadcasts a message over a server instance.
     * @param permission The permission the receivers need to have.
     * @param message The message to broadcast.
     */
    void broadcast(String permission, String message);
}
