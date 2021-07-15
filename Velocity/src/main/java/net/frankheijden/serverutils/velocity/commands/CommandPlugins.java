package net.frankheijden.serverutils.velocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.velocitypowered.api.command.CommandSource;
import net.frankheijden.serverutils.common.commands.Plugins;
import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginManager;
import net.frankheijden.serverutils.velocity.utils.VelocityUtils;

@CommandAlias("vpl|vplugins|velocitypl")
public class CommandPlugins extends BaseCommand {

    private static final VelocityPluginManager manager = VelocityPluginManager.get();

    /**
     * Sends the plugin list to the sender.
     * The `-v` flag will output the plugins with version.
     * The `-m` flag will also output modules in the plugin list.
     * @param sender The sender of the command.
     */
    @Default
    @CommandCompletion("-v")
    @CommandPermission("serverutils.plugins")
    @Description("Shows the plugins of this proxy.")
    public void onPlugins(CommandSource sender, String... args) {
        boolean version = contains(args, "-v");
        Plugins.sendPlugins(VelocityUtils.wrap(sender), manager.getPluginsSorted(), pl -> {
            String ver = version ? Messenger.getMessage("serverutils.plugins.version",
                    "%version%", pl.getDescription().getVersion().orElse("<UNKNOWN>")) : "";
            return Messenger.getMessage("serverutils.plugins.format",
                    "%plugin%", pl.getDescription().getId()) + ver;
        });
    }

    private static boolean contains(String[] arr, String val) {
        for (String s : arr) {
            if (s.equalsIgnoreCase(val)) return true;
        }
        return false;
    }
}
