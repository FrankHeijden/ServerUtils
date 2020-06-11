package net.frankheijden.serverutils.config;

import net.frankheijden.serverutils.ServerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messenger {

    private static final Defaults DEFAULT_MESSAGES = Defaults.of(
            "serverutils", Defaults.of(
                    "success", "&3Successfully %action%ed &b%what%&3!",
                    "warning", "&3Successfully %action%ed &b%what%&3, but with warnings.",
                    "error", "&cAn error occurred while %action%ing &4%what%&c, please check the console!",
                    "not_exists", "&cAn error occurred while %action%ing &4%what%&c, plugin does not exist!",
                    "not_enabled", "&cAn error occurred while %action%ing &4%what%&c, plugin is not enabled!",
                    "already_enabled", "&cAn error occurred while %action%ing &4%what%&c, plugin is already enabled!",
                    "file_changed", "&cAccessing the jar file while %action%ing &4%what%&c went wrong, please load the plugin manually!",
                    "invalid_description", "&cAn error occurred while %action%ing &4%what%&c, plugin doesn't have a valid description!",
                    "update", Defaults.of(
                            "available", "&8&m------------=&r&8[ &b&lServerUtils Update&r &8]&m=--------------\n"
                                    + " &3Current version: &b%old%\n"
                                    + " &3New version: &b%new%\n"
                                    + " &3Release info: &b%info%\n"
                                    + "&8&m-------------------------------------------------",
                            "downloading", "&8&m------------=&r&8[ &b&lServerUtils Update&r &8]&m=--------------\n"
                                    + " &3A new version of ServerUtils will be downloaded and installed after a restart!\n"
                                    + " &3Current version: &b%old%\n"
                                    + " &3New version: &b%new%\n"
                                    + " &3Release info: &b%info%\n"
                                    + "&8&m-------------------------------------------------",
                            "download_failed", "&cFailed to download version %new% of ServerUtils. Please update manually.",
                            "download_success", "&3ServerUtils has been downloaded and will be installed on the next restart."
                    ),
                    "help", Defaults.of(
                            "header", "&8&m-------------=&r&8[ &b&lServerUtils Help&r &8]&m=---------------",
                            "format", "&8/&3%command%&b%subcommand% &f(&7%help%&f)",
                            "footer", "&8&m-------------------------------------------------"
                    ),
                    "plugins", Defaults.of(
                            "header", "&8&m------------=&r&8[ &b&lServerUtils Plugins&r &8]&m=-------------",
                            "format", "&3%plugin%",
                            "format_disabled", "&c%plugin%",
                            "seperator", "&b, ",
                            "last_seperator", " &band ",
                            "version", " &8(&a%version%&8)",
                            "footer", "&8&m-------------------------------------------------"
                    ),
                    "plugininfo", Defaults.of(
                            "header", "&8&m-----------=&r&8[ &b&lServerUtils PluginInfo&r &8]&m=-----------",
                            "format", " &3%key%&8: &b%value%",
                            "list_format", "&b%value%",
                            "seperator", "&8, ",
                            "last_seperator", " &8and ",
                            "footer", "&8&m-------------------------------------------------"
                    ),
                    "commandinfo", Defaults.of(
                            "header", "&8&m-----------=&r&8[ &b&lServerUtils CommandInfo&r &8]&m=----------",
                            "format", " &3%key%&8: &b%value%",
                            "list_format", "&b%value%",
                            "seperator", "&8, ",
                            "last_seperator", " &8and ",
                            "footer", "&8&m-------------------------------------------------",
                            "not_exists", "&cThat command is not a valid registered command."
                    )
            )
    );

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static Messenger instance;
    private final YamlConfiguration messages;

    public Messenger(File file) {
        instance = this;
        messages = Defaults.init(file, DEFAULT_MESSAGES);
    }

    public static String getMessage(String path, String... replacements) {
        String message = instance.messages.getString(path);
        if (message != null) {
            return apply(message, replacements);
        } else {
            plugin.getLogger().severe("Missing locale in messages.yml at path '" + path + "'!");
        }
        return null;
    }

    public static String apply(String message, String... replacements) {
        if (message == null || message.isEmpty()) return null;
        for (int i = 0; i < replacements.length; i++, i++) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    public static void sendRawMessage(CommandSender sender, String msg, String... replacements) {
        String message = apply(msg, replacements);
        if (message != null) {
            sender.sendMessage(color(message));
        }
    }

    public static void sendMessage(CommandSender sender, String path, String... replacements) {
        String message = getMessage(path, replacements);
        if (message != null) {
            sender.sendMessage(color(message));
        }
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
