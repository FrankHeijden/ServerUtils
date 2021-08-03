package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitAudience extends ServerUtilsAudience<CommandSender> {

    public BukkitAudience(Audience audience, CommandSender source) {
        super(audience, source);
    }

    @Override
    public boolean isPlayer() {
        return source instanceof Player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }
}
