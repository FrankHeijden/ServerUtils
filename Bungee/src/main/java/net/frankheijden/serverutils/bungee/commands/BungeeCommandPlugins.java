package net.frankheijden.serverutils.bungee.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.frankheijden.serverutils.bungee.entities.BungeeCommandSender;
import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class BungeeCommandPlugins extends CommandPlugins<BungeePlugin, Plugin, BungeeCommandSender> {

    public BungeeCommandPlugins(BungeePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void register(
            CommandManager<BungeeCommandSender> manager,
            Command.Builder<BungeeCommandSender> builder
    ) {
        manager.command(builder
                .flag(parseFlag("version"))
                .flag(parseFlag("modules"))
                .handler(this::handlePlugins));
    }

    @Override
    protected void handlePlugins(CommandContext<BungeeCommandSender> context) {
        BungeeCommandSender sender = context.getSender();
        boolean hasVersionFlag = context.flags().contains("version");
        boolean hasModulesFlag = context.flags().contains("modules");

        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(hasModulesFlag), bungeePlugin -> {
            PluginDescription description = bungeePlugin.getDescription();

            String message = plugin.getMessagesResource().getMessage(
                    "serverutils.plugins.format",
                    "%plugin%", description.getName()
            );

            if (hasVersionFlag) {
                message += plugin.getMessagesResource().getMessage(
                        "serverutils.plugins.version",
                        "%version%", description.getVersion()
                );
            }

            return message;
        });
    }
}
