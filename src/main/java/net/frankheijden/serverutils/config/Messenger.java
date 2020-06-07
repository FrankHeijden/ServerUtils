package net.frankheijden.serverutils.config;

import net.frankheijden.serverutils.ServerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Messenger {

    private static final Defaults DEFAULT_MESSAGES;
    static {
        DEFAULT_MESSAGES = Defaults.of(
                "serverutils", Defaults.of(
                        "success", "&3Successfully %action%ed &b%what%&3!",
                        "warning", "&3Successfully %action%ed &b%what%&3, but with warnings.",
                        "error", "&cAn error occurred while %action%ing &4%what%&c, please check the console!",
                        "not_exists", "&cAn error occurred while %action%ing &4%what%&c, plugin does not exist!",
                        "already_enabled", "&cAn error occurred while %action%ing &4%what%&c, plugin is already enabled!",
                        "help", Defaults.of(
                                "header", "&8&m-------------=&r&8[ &b&lServerUtils Help&r &8]&m=---------------",
                                "format", "&8/&3%command%&b%subcommand% &f(&7%help%&f)",
                                "footer", "&8&m-------------------------------------------------"
                        ),
                        "plugins", Defaults.of(
                                "header", "&8&m------------=&r&8[ &b&lServerUtils Plugins&r &8]&m=-------------",
                                "format", "&3%plugin%",
                                "seperator", "&b, ",
                                "last_seperator", " &band ",
                                "version", " &8(&a%version%&8)",
                                "footer", "&8&m-------------------------------------------------"
                        ),
                        "plugininfo", Defaults.of(
                                "header", "&8&m-----------=&r&8[ &b&lServerUtils PluginInfo&r &8]&m=-----------",
                                "format", " &3%key%&8: &b%value%",
                                "seperator", "&8, ",
                                "last_seperator", " &8and ",
                                "footer", "&8&m-------------------------------------------------"
                        )
                )
        );
    }

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static Messenger instance;
    private final YamlConfiguration messages;

    public Messenger(File file) {
        instance = this;
        messages = YamlConfiguration.loadConfiguration(file);
        Defaults.addDefaults(DEFAULT_MESSAGES, messages);

        try {
            // Idk somehow the order messes up
            // of the messages if we don't do this
            file.delete();
            file.createNewFile();

            messages.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
