package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.common.providers.ServerUtilsAudienceProvider;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;

public class BungeeAudienceProvider implements ServerUtilsAudienceProvider<CommandSender> {

    private final BungeeAudiences audiences;
    private final BungeeAudience consoleServerAudience;

    /**
     * Constructs a new BungeeAudienceProvider.
     */
    public BungeeAudienceProvider(ServerUtils plugin, BungeeAudiences audiences) {
        this.audiences = audiences;
        this.consoleServerAudience = new BungeeAudience(
                audiences.console(),
                plugin.getProxy().getConsole()
        );
    }

    @Override
    public BungeeAudience getConsoleServerAudience() {
        return consoleServerAudience;
    }

    @Override
    public BungeeAudience get(CommandSender source) {
        return new BungeeAudience(audiences.sender(source), source);
    }

    @Override
    public void broadcast(Component component, String permission) {
        audiences.filter(sender -> sender.hasPermission(permission)).sendMessage(component);
    }
}
