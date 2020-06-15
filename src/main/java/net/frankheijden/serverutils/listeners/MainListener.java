package net.frankheijden.serverutils.listeners;

import net.frankheijden.serverutils.config.Config;
import net.frankheijden.serverutils.tasks.UpdateCheckerTask;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    private static final Config config = Config.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!config.getBoolean("settings.check-updates")) return;

        Player player = event.getPlayer();
        if (player.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.start(player);
        }
    }
}
