package net.frankheijden.serverutils.common.listeners;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;

public abstract class ServerListener<
        U extends ServerUtilsPlugin<P, T, C, S>,
        P,
        T,
        C extends ServerCommandSender<S>,
        S
        > extends ServerUtilsListener<U, P, T, C, S> {

    protected ServerListener(U plugin) {
        super(plugin);
    }

    /**
     * Handles the update check on the given ServerCommandSender.
     * @param sender The sender which triggered the update.
     */
    protected void handleUpdate(C sender) {
        if (sender.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.tryStart(sender, "login");
        }
    }
}
