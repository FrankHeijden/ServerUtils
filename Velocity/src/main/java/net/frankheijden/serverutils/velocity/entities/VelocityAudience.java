package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class VelocityAudience extends ServerUtilsAudience<CommandSource> {

    protected VelocityAudience(Audience audience, CommandSource source) {
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

    @Override
    public void sendMessage(Component component) {
        source.sendMessage(component);
    }
}
