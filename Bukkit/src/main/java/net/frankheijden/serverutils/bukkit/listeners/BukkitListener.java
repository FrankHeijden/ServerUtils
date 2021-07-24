package net.frankheijden.serverutils.bukkit.listeners;

import net.frankheijden.serverutils.bukkit.entities.BukkitCommandSender;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.common.listeners.ServerListener;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitListener extends ServerListener<
        BukkitPlugin,
        Plugin,
        BukkitTask,
        BukkitCommandSender,
        CommandSender
        > implements Listener {

    public BukkitListener(BukkitPlugin plugin) {
        super(plugin);
    }

    /**
     * Called when a player joins the server.
     * Used for sending an update message to the player, if enabled and has permission.
     * @param event The PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        handleUpdate(plugin.getChatProvider().get(event.getPlayer()));
    }
}
