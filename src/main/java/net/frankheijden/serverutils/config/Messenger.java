package net.frankheijden.serverutils.config;

import net.frankheijden.serverutils.ServerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messenger {

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static Messenger instance;
    private final YamlConfiguration messages;

    public Messenger(File file) {
        instance = this;
        messages = YamlConfiguration.loadConfiguration(file);
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
