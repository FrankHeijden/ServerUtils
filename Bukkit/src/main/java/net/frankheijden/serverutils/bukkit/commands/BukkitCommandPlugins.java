package net.frankheijden.serverutils.bukkit.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.frankheijden.serverutils.bukkit.entities.BukkitAudience;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.bukkit.entities.BukkitPluginDescription;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("LineLength")
public class BukkitCommandPlugins extends CommandPlugins<BukkitPlugin, Plugin, BukkitAudience, BukkitPluginDescription> {

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

        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(), hasVersionFlag);
    }
}
