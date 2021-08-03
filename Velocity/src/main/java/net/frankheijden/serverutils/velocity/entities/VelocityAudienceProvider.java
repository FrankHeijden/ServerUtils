package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.frankheijden.serverutils.common.providers.ServerUtilsAudienceProvider;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.kyori.adventure.text.Component;

public class VelocityAudienceProvider implements ServerUtilsAudienceProvider<CommandSource> {

    private final ServerUtils plugin;
    private final VelocityAudience consoleServerAudience;

    /**
     * Constructs a new VelocityAudienceProvider.
     */
    public VelocityAudienceProvider(ServerUtils plugin) {
        this.plugin = plugin;
        this.consoleServerAudience = new VelocityAudience(
                plugin.getProxy().getConsoleCommandSource(),
                plugin.getProxy().getConsoleCommandSource()
        );
    }

    @Override
    public VelocityAudience getConsoleServerAudience() {
        return consoleServerAudience;
    }

    @Override
    public VelocityAudience get(CommandSource source) {
        return new VelocityAudience(source, source);
    }

    @Override
    public void broadcast(Component component, String permission) {
        for (Player player : plugin.getProxy().getAllPlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(component);
            }
        }
    }
}
