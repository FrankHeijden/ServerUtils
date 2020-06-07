package net.frankheijden.serverutils.listeners;

import net.frankheijden.serverutils.tasks.UpdateCheckerTask;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.start(player);
        }
    }
}
