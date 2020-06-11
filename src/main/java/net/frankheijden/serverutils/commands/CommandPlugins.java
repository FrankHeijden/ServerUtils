package net.frankheijden.serverutils.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frankheijden.serverutils.config.Messenger;
import net.frankheijden.serverutils.utils.ListBuilder;
import net.frankheijden.serverutils.utils.ListFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

@CommandAlias("plugins|pl")
public class CommandPlugins extends BaseCommand {

    @Default
    @CommandPermission("serverutils.plugins")
    @Description("Shows the plugins of this server.")
    public void onPlugins(CommandSender sender) {
        sendPlugins(sender, pl -> {
            String format = "serverutils.plugins.format" + (pl.isEnabled() ? "" : "_disabled");
            return Messenger.getMessage(format, "%plugin%", pl.getName());
        });
    }

    @Subcommand("-v")
    @CommandPermission("serverutils.plugins.version")
    @Description("Shows the plugins of this server with version.")
    public void onPluginsWithVersion(CommandSender sender) {
        sendPlugins(sender, pl -> {
            String format = "serverutils.plugins.format" + (pl.isEnabled() ? "" : "_disabled");
            String version = Messenger.getMessage("serverutils.plugins.version", "%version%", pl.getDescription().getVersion());
            return Messenger.getMessage(format, "%plugin%", pl.getName()) + version;
        });
    }

    private static void sendPlugins(CommandSender sender, ListFormat<Plugin> pluginFormat) {
        Messenger.sendMessage(sender, "serverutils.plugins.header");
        sender.sendMessage(Messenger.color(ListBuilder.create(Arrays.asList(Bukkit.getPluginManager().getPlugins()))
                .seperator(Messenger.getMessage("serverutils.plugins.seperator"))
                .lastSeperator(Messenger.getMessage("serverutils.plugins.last_seperator"))
                .format(pluginFormat)
                .toString()));
        Messenger.sendMessage(sender, "serverutils.plugins.footer");
    }
}
