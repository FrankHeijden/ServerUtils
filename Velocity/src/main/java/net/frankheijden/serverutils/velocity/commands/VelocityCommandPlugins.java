package net.frankheijden.serverutils.velocity.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.velocity.entities.VelocityAudience;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;

public class VelocityCommandPlugins extends CommandPlugins<VelocityPlugin, PluginContainer, VelocityAudience> {

    public VelocityCommandPlugins(VelocityPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void register(
            CommandManager<VelocityAudience> manager,
            Command.Builder<VelocityAudience> builder
    ) {
        manager.command(builder
                .flag(parseFlag("version"))
                .handler(this::handlePlugins));
    }

    @Override
    protected void handlePlugins(CommandContext<VelocityAudience> context) {
        VelocityAudience sender = context.getSender();
        boolean hasVersionFlag = context.flags().contains("version");

        MessagesResource messages = plugin.getMessagesResource();
        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(), container -> {
            PluginDescription description = container.getDescription();

            TextComponent.Builder builder = Component.text();
            builder.append(messages.get(MessageKey.PLUGINS_FORMAT).toComponent(
                    Template.of("plugin", description.getId())
            ));
            if (hasVersionFlag) {
                builder.append(messages.get(MessageKey.PLUGINS_VERSION).toComponent(
                        Template.of("version", description.getVersion().orElse("<UNKNOWN>"))
                ));
            }

            return builder.build();
        });
    }
}
