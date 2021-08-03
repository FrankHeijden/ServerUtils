package net.frankheijden.serverutils.bungee.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.frankheijden.serverutils.bungee.entities.BungeeAudience;
import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class BungeeCommandPlugins extends CommandPlugins<BungeePlugin, Plugin, BungeeAudience> {

    public BungeeCommandPlugins(BungeePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void register(
            CommandManager<BungeeAudience> manager,
            Command.Builder<BungeeAudience> builder
    ) {
        manager.command(builder
                .flag(parseFlag("version"))
                .flag(parseFlag("modules"))
                .handler(this::handlePlugins));
    }

    @Override
    protected void handlePlugins(CommandContext<BungeeAudience> context) {
        BungeeAudience sender = context.getSender();
        boolean hasVersionFlag = context.flags().contains("version");
        boolean hasModulesFlag = context.flags().contains("modules");

        MessagesResource messages = plugin.getMessagesResource();
        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(hasModulesFlag), bungeePlugin -> {
            PluginDescription description = bungeePlugin.getDescription();

            TextComponent.Builder builder = Component.text();
            builder.append(messages.get(MessageKey.PLUGINS_FORMAT).toComponent(
                    Template.of("plugin", description.getName())
            ));
            if (hasVersionFlag) {
                builder.append(messages.get(MessageKey.PLUGINS_FORMAT).toComponent(
                        Template.of("version", description.getVersion())
                ));
            }

            return builder.build();
        });
    }
}
