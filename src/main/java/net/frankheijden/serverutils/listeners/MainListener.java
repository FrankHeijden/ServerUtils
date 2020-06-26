package net.frankheijden.serverutils.listeners;

import net.frankheijden.serverutils.config.Config;
import net.frankheijden.serverutils.tasks.UpdateCheckerTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    private static final Config config = Config.getInstance();

    /**
     * Called when a player joins the server.
     * Used for sending an update message to the player, if enabled and has permission.
     * @param event The PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!config.getBoolean("settings.check-updates")) return;

        Player player = event.getPlayer();
        if (player.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.start(player);
        }
    }
}
