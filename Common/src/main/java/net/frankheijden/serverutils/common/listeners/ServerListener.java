package net.frankheijden.serverutils.common.listeners;

import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.config.YamlConfig;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;

public class ServerListener {

    private static final YamlConfig config = Config.getInstance().getConfig();

    /**
     * Handles the update check on the given ServerCommandSender.
     * @param sender The sender which triggered the update.
     */
    public static void handleUpdate(ServerCommandSender sender) {
        if (!config.getBoolean("settings.check-updates-login")) return;

        if (sender.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.start(sender);
        }
    }
}
