package net.frankheijden.serverutils.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.frankheijden.serverutils.config.Messenger;
import net.frankheijden.serverutils.utils.ListBuilder;
import net.frankheijden.serverutils.utils.ListFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@CommandAlias("plugins|pl")
public class CommandPlugins extends BaseCommand {

    /**
     * Sends the plugin list to the sender, without plugin version.
     * @param sender The sender of the command.
     */
    @Default
    @CommandPermission("serverutils.plugins")
    @Description("Shows the plugins of this server.")
    public void onPlugins(CommandSender sender) {
        sendPlugins(sender, pl -> {
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
        sendPlugins(sender, pl -> {
            String format = "serverutils.plugins.format" + (pl.isEnabled() ? "" : "_disabled");
            String version = Messenger.getMessage("serverutils.plugins.version",
                    "%version%", pl.getDescription().getVersion());
            return Messenger.getMessage(format, "%plugin%", pl.getName()) + version;
        });
    }

    private static void sendPlugins(CommandSender sender, ListFormat<Plugin> pluginFormat) {
        Messenger.sendMessage(sender, "serverutils.plugins.header");
        sender.sendMessage(Messenger.color(ListBuilder.create(getPluginsSorted())
                .seperator(Messenger.getMessage("serverutils.plugins.seperator"))
                .lastSeperator(Messenger.getMessage("serverutils.plugins.last_seperator"))
                .format(pluginFormat)
                .toString()));
        Messenger.sendMessage(sender, "serverutils.plugins.footer");
    }

    private static List<Plugin> getPluginsSorted() {
        List<Plugin> plugins = Arrays.asList(Bukkit.getPluginManager().getPlugins());
        plugins.sort(Comparator.comparing(Plugin::getName));
        return plugins;
    }
}
