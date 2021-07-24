package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityCommandSender implements ServerCommandSender<CommandSource> {

    private final CommandSource source;

    public VelocityCommandSender(CommandSource source) {
        this.source = source;
    }

    @Override
    public void sendMessage(String message) {
        source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    /**
     * Whether or not the given instance is a player.
     * @return Boolean true or false.
     */
    @Override
    public boolean isPlayer() {
        return source instanceof Player;
    }

    @Override
    public CommandSource getSource() {
        return source;
    }
}
