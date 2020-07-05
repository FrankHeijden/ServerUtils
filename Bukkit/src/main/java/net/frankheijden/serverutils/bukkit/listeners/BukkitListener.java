package net.frankheijden.serverutils.bukkit.listeners;

import net.frankheijden.serverutils.common.listeners.ServerListener;
import net.frankheijden.serverutils.bukkit.utils.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {

    /**
     * Called when a player joins the server.
     * Used for sending an update message to the player, if enabled and has permission.
     * @param event The PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ServerListener.handleUpdate(BukkitUtils.wrap(event.getPlayer()));
    }
}
