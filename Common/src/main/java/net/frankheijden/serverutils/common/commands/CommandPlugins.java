package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.context.CommandContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.utils.ListComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;

@SuppressWarnings("LineLength")
public abstract class CommandPlugins<U extends ServerUtilsPlugin<P, ?, C, ?, D>, P, C extends ServerUtilsAudience<?>, D extends ServerUtilsPluginDescription>
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
    protected void handlePlugins(C sender, List<P> plugins, boolean hasVersionFlag) {
        List<P> filteredPlugins = new ArrayList<>(plugins.size());
        Set<String> hiddenPlugins = new HashSet<>(plugin.getConfigResource().getConfig().getStringList(
                "hide-plugins-from-plugins-command"
        ));
        AbstractPluginManager<P, D> pluginManager = plugin.getPluginManager();
        for (P plugin : plugins) {
            if (!hiddenPlugins.contains(pluginManager.getPluginId(plugin))) {
                filteredPlugins.add(plugin);
            }
        }

        MessagesResource messages = plugin.getMessagesResource();

        sender.sendMessage(messages.get(MessageKey.PLUGINS_HEADER).toComponent());
        TextComponent.Builder builder = Component.text();
        builder.append(messages.get(MessageKey.PLUGINS_PREFIX).toComponent(
                Template.of("count", String.valueOf(filteredPlugins.size()))
        ));
        builder.append(ListComponentBuilder.create(filteredPlugins)
                .separator(messages.get(MessageKey.PLUGINS_SEPARATOR).toComponent())
                .lastSeparator(messages.get(MessageKey.PLUGINS_LAST_SEPARATOR).toComponent())
                .format(plugin -> {
                    D description = pluginManager.getLoadedPluginDescription(plugin);

                    TextComponent.Builder formatBuilder = Component.text();
                    MessageKey formatKey = pluginManager.isPluginEnabled(plugin)
                            ? MessageKey.PLUGINS_FORMAT
                            : MessageKey.PLUGINS_FORMAT_DISABLED;
                    formatBuilder.append(messages.get(formatKey).toComponent(
                            Template.of("plugin", description.getName())
                    ));
                    if (hasVersionFlag) {
                        formatBuilder.append(messages.get(MessageKey.PLUGINS_VERSION).toComponent(
                                Template.of("version", description.getVersion())
                        ));
                    }

                    return formatBuilder.build();
                })
                .build());
        sender.sendMessage(builder.build());
        sender.sendMessage(messages.get(MessageKey.PLUGININFO_FOOTER).toComponent());
    }
}
