package net.frankheijden.serverutils.common.listeners;

import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;

public abstract class PlayerListener<U extends ServerUtilsPlugin<P, ?, C, ?, ?>, P, C extends ServerUtilsAudience<?>>
        extends ServerUtilsListener<U, C> {

    protected PlayerListener(U plugin) {
        super(plugin);
    }

    /**
     * Handles the update check on the given ServerCommandSender.
     * @param sender The sender which triggered the update.
     */
    protected void handleUpdate(C sender) {
        if (sender.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.tryStart(plugin, sender, "login");
        }
    }
}
