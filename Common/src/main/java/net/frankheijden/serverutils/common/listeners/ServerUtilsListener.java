package net.frankheijden.serverutils.common.listeners;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public abstract class ServerUtilsListener<U extends ServerUtilsPlugin<?, ?, C, ?, ?>, C extends ServerCommandSender<?>> {

    protected final U plugin;

    protected ServerUtilsListener(U plugin) {
        this.plugin = plugin;
    }
}
