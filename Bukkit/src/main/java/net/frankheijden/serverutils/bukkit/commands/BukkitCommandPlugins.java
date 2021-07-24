package net.frankheijden.serverutils.bukkit.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.frankheijden.serverutils.bukkit.entities.BukkitCommandSender;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitCommandPlugins extends CommandPlugins<BukkitPlugin, Plugin, BukkitCommandSender> {

    public BukkitCommandPlugins(BukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void register(
            CommandManager<BukkitCommandSender> manager,
            Command.Builder<BukkitCommandSender> builder
    ) {
        manager.command(builder
                .flag(parseFlag("version"))
                .handler(this::handlePlugins));
    }

    @Override
    protected void handlePlugins(CommandContext<BukkitCommandSender> context) {
        BukkitCommandSender sender = context.getSender();
        boolean hasVersionFlag = context.flags().contains("version");

        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(), bukkitPlugin -> {
            PluginDescriptionFile description = bukkitPlugin.getDescription();

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
