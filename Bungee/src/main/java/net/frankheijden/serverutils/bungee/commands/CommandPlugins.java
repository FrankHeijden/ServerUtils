package net.frankheijden.serverutils.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeePluginProvider;
import net.frankheijden.serverutils.bungee.utils.BungeeUtils;
import net.frankheijden.serverutils.common.commands.Plugins;
import net.frankheijden.serverutils.common.config.Messenger;
import net.md_5.bungee.api.CommandSender;

@CommandAlias("bpl|bplugins|bungeepl")
public class CommandPlugins extends BaseCommand {

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static final BungeePluginProvider provider = (BungeePluginProvider) plugin.getPlugin().getPluginProvider();

    /**
     * Sends the plugin list to the sender.
     * The `-v` flag will output the plugins with version.
     * The `-m` flag will also output modules in the plugin list.
     * @param sender The sender of the command.
     */
    @Default
    @CommandCompletion("-v|-m -v|-m")
    @CommandPermission("serverutils.plugins")
    @Description("Shows the plugins of this proxy.")
    public void onPlugins(CommandSender sender, String... args) {
        boolean version = contains(args, "-v");
        boolean modules = contains(args, "-m");
        Plugins.sendPlugins(BungeeUtils.wrap(sender), provider.getPluginsSorted(modules), pl -> {
            String ver = version ? Messenger.getMessage("serverutils.plugins.version",
                    "%version%", pl.getDescription().getVersion()) : "";
            return Messenger.getMessage("serverutils.plugins.format",
                    "%plugin%", pl.getDescription().getName()) + ver;
        });
    }

    private static boolean contains(String[] arr, String val) {
        for (String s : arr) {
            if (s.equalsIgnoreCase(val)) return true;
        }
        return false;
    }
}
