package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.context.CommandContext;
import java.util.List;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.common.utils.ListFormat;

public abstract class CommandPlugins<U extends ServerUtilsPlugin<P, ?, C, ?>, P, C extends ServerCommandSender<?>>
        extends ServerUtilsCommand<U, C> {

    protected CommandPlugins(U plugin) {
        super(plugin, "plugins");
    }

    protected abstract void handlePlugins(CommandContext<C> context);

    /**
     * Sends a plugin list to the receiver.
     * @param sender The receiver of the plugin list.
     * @param plugins The plugins to be sent.
     * @param pluginFormat The format of the plugins to be sent.
     * @param <T> The plugin type.
     */
    protected void handlePlugins(C sender, List<P> plugins, ListFormat<P> pluginFormat) {
        String prefix = plugin.getMessagesResource().getMessage(
                "serverutils.plugins.prefix",
                "%count%", String.valueOf(plugins.size())
        );
        if (prefix == null) prefix = "";

        plugin.getMessagesResource().sendMessage(sender, "serverutils.plugins.header");
        sender.sendMessage(plugin.getChatProvider().color(prefix + ListBuilder.create(plugins)
                .seperator(plugin.getMessagesResource().getMessage("serverutils.plugins.seperator"))
                .lastSeperator(plugin.getMessagesResource().getMessage("serverutils.plugins.last_seperator"))
                .format(pluginFormat)
                .toString()));
        plugin.getMessagesResource().sendMessage(sender, "serverutils.plugins.footer");
    }
}
