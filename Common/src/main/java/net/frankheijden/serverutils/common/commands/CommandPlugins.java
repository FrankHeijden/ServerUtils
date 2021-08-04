package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.context.CommandContext;
import java.util.List;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.utils.ListComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;

public abstract class CommandPlugins<U extends ServerUtilsPlugin<P, ?, C, ?, ?>, P, C extends ServerUtilsAudience<?>>
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
     */
    protected void handlePlugins(C sender, List<P> plugins, ListComponentBuilder.Format<P> pluginFormat) {
        MessagesResource messages = plugin.getMessagesResource();

        sender.sendMessage(messages.get(MessageKey.PLUGINS_HEADER).toComponent());
        TextComponent.Builder builder = Component.text();
        builder.append(messages.get(MessageKey.PLUGINS_PREFIX).toComponent(
                Template.of("count", String.valueOf(plugins.size()))
        ));
        builder.append(ListComponentBuilder.create(plugins)
                .separator(messages.get(MessageKey.PLUGINS_SEPARATOR).toComponent())
                .lastSeparator(messages.get(MessageKey.PLUGINS_LAST_SEPARATOR).toComponent())
                .format(pluginFormat)
                .build());
        sender.sendMessage(builder.build());
        sender.sendMessage(messages.get(MessageKey.PLUGININFO_FOOTER).toComponent());
    }
}
