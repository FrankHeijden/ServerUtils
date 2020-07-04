package net.frankheijden.serverutils.bukkit.listeners;

import net.frankheijden.serverutils.bukkit.tasks.UpdateCheckerTask;
import net.frankheijden.serverutils.bukkit.utils.BukkitUtils;
import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.config.YamlConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    private static final YamlConfig config = Config.getInstance().getConfig();

    /**
     * Called when a player joins the server.
     * Used for sending an update message to the player, if enabled and has permission.
     * @param event The PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!config.getBoolean("settings.check-updates-login")) return;

        Player player = event.getPlayer();
        if (player.hasPermission("serverutils.notification.update")) {
            UpdateCheckerTask.start(BukkitUtils.wrap(player));
        }
    }
}
