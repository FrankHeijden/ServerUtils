package net.frankheijden.serverutils.common.commands;

import java.util.List;

import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.common.utils.ListFormat;

public class Plugins {

    /**
     * Sends a plugin list to the receiver.
     * @param sender The receiver of the plugin list.
     * @param plugins The plugins to be sent.
     * @param pluginFormat The format of the plugins to be sent.
     * @param <T> The plugin type.
     */
    public static <T> void sendPlugins(ServerCommandSender sender, List<T> plugins, ListFormat<T> pluginFormat) {
        Messenger.sendMessage(sender, "serverutils.plugins.header");
        String prefix = Messenger.getMessage("serverutils.plugins.prefix",
                "%count%", String.valueOf(plugins.size()));
        sender.sendMessage(Messenger.color(prefix + ListBuilder.create(plugins)
                .seperator(Messenger.getMessage("serverutils.plugins.seperator"))
                .lastSeperator(Messenger.getMessage("serverutils.plugins.last_seperator"))
                .format(pluginFormat)
                .toString()));
        Messenger.sendMessage(sender, "serverutils.plugins.footer");
    }
}
