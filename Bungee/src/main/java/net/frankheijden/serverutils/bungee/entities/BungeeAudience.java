package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeAudience extends ServerUtilsAudience<CommandSender> {

    protected BungeeAudience(Audience audience, CommandSender source) {
        super(audience, source);
    }

    @Override
    public boolean isPlayer() {
        return source instanceof ProxiedPlayer;
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }
}
