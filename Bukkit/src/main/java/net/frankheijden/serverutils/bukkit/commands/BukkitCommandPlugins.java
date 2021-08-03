package net.frankheijden.serverutils.bukkit.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.frankheijden.serverutils.bukkit.entities.BukkitAudience;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitCommandPlugins extends CommandPlugins<BukkitPlugin, Plugin, BukkitAudience> {

    public BukkitCommandPlugins(BukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void register(
            CommandManager<BukkitAudience> manager,
            Command.Builder<BukkitAudience> builder
    ) {
        manager.command(builder
                .flag(parseFlag("version"))
                .handler(this::handlePlugins));
    }

    @Override
    protected void handlePlugins(CommandContext<BukkitAudience> context) {
        BukkitAudience sender = context.getSender();
        boolean hasVersionFlag = context.flags().contains("version");

        MessagesResource messages = plugin.getMessagesResource();
        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(), bukkitPlugin -> {
            PluginDescriptionFile description = bukkitPlugin.getDescription();

            TextComponent.Builder builder = Component.text();
            builder.append(messages.get(MessageKey.PLUGINS_FORMAT).toComponent(
                    Template.of("plugin", description.getName())
            ));
            if (hasVersionFlag) {
                builder.append(messages.get(MessageKey.PLUGINS_VERSION).toComponent(
                        Template.of("version", description.getVersion())
                ));
            }

            return builder.build();
        });
    }
}
