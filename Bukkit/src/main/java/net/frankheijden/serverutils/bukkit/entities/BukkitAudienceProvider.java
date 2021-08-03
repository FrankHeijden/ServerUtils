package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.providers.ServerUtilsAudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class BukkitAudienceProvider implements ServerUtilsAudienceProvider<CommandSender> {

    private final BukkitAudiences audiences;
    private final BukkitAudience consoleServerAudience;

    /**
     * Constructs a new BukkitAudienceProvider.
     */
    public BukkitAudienceProvider(ServerUtils plugin, BukkitAudiences audiences) {
        this.audiences = audiences;
        this.consoleServerAudience = new BukkitAudience(
                audiences.console(),
                plugin.getServer().getConsoleSender()
        );
    }

    @Override
    public BukkitAudience getConsoleServerAudience() {
        return this.consoleServerAudience;
    }

    @Override
    public BukkitAudience get(CommandSender source) {
        return new BukkitAudience(audiences.sender(source), source);
    }

    @Override
    public void broadcast(Component component, String permission) {
        audiences.filter(sender -> sender.hasPermission(permission)).sendMessage(component);
    }
}
