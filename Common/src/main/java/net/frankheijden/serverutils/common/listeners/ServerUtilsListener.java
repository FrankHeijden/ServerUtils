package net.frankheijden.serverutils.common.listeners;

import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public abstract class ServerUtilsListener<U extends ServerUtilsPlugin<?, ?, C, ?, ?>, C extends ServerUtilsAudience<?>> {

    protected final U plugin;

    protected ServerUtilsListener(U plugin) {
        this.plugin = plugin;
    }
}
