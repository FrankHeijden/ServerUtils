package net.frankheijden.serverutils.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.utils.BukkitUtils;
import net.frankheijden.serverutils.common.commands.Plugins;
import net.frankheijden.serverutils.common.config.Messenger;
import org.bukkit.command.CommandSender;

@CommandAlias("pl|plugins")
public class CommandPlugins extends BaseCommand {

    private static final BukkitPluginManager manager = BukkitPluginManager.get();

    /**
     * Sends the plugin list to the sender, without plugin version.
     * @param sender The sender of the command.
     */
    @Default
    @CommandPermission("serverutils.plugins")
    @Description("Shows the plugins of this server.")
    public void onPlugins(CommandSender sender) {
        Plugins.sendPlugins(BukkitUtils.wrap(sender), manager.getPluginsSorted(), pl -> {
            String format = "serverutils.plugins.format" + (pl.isEnabled() ? "" : "_disabled");
            return Messenger.getMessage(format, "%plugin%", pl.getName());
        });
    }

    /**
     * Sends the plugin list to the sender, with plugin version.
     * @param sender The sender of the command.
     */
    @Subcommand("-v")
    @CommandPermission("serverutils.plugins.version")
    @Description("Shows the plugins of this server with version.")
    public void onPluginsWithVersion(CommandSender sender) {
        Plugins.sendPlugins(BukkitUtils.wrap(sender), manager.getPluginsSorted(), pl -> {
            String format = "serverutils.plugins.format" + (pl.isEnabled() ? "" : "_disabled");
            String version = Messenger.getMessage("serverutils.plugins.version",
                    "%version%", pl.getDescription().getVersion());
            return Messenger.getMessage(format, "%plugin%", pl.getName()) + version;
        });
    }
}
