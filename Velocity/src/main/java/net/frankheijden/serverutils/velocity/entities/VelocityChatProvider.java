package net.frankheijden.serverutils.velocity.entities;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.providers.ChatProvider;
import net.frankheijden.serverutils.common.utils.HexUtils;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.frankheijden.serverutils.velocity.utils.VelocityUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityChatProvider extends ChatProvider {

    private final ServerUtils plugin;

    public VelocityChatProvider(ServerUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public ServerCommandSender getConsoleSender() {
        return VelocityUtils.wrap(plugin.getProxy().getConsoleCommandSource());
    }

    @Override
    public String color(String str) {
        return HexUtils.convertHexString(str);
    }

    @Override
    public void broadcast(String permission, String message) {
        Component msg = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        plugin.getProxy().getAllPlayers().stream()
                .filter(p -> p.hasPermission(permission))
                .forEach(p -> p.sendMessage(msg));
    }
}
