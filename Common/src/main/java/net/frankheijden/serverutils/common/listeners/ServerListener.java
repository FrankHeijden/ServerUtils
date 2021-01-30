package net.frankheijden.serverutils.common.listeners;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;

public class ServerListener {

    /**
     * Handles the update check on the given ServerCommandSender.
     * @param sender The sender which triggered the update.
     */
    public static void handleUpdate(ServerCommandSender sender) {
        if (sender.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.tryStart(sender, "login");
        }
    }
}
