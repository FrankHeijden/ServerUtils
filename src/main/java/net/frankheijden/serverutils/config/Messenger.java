package net.frankheijden.serverutils.config;

import java.io.File;

import net.frankheijden.serverutils.ServerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger extends YamlResource {

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static Messenger instance;

    public Messenger(File file) {
        super(file, "messages.yml");
        instance = this;
    }

    /**
     * Retrieves a message from the config.
     * @param path The yml path to the message.
     * @param replacements The replacements to be taken into account.
     * @return The config message with translated placeholders.
     */
    public static String getMessage(String path, String... replacements) {
        String message = instance.getConfiguration().getString(path);
        if (message != null) {
            return apply(message, replacements);
        } else {
            plugin.getLogger().severe("Missing locale in messages.yml at path '" + path + "'!");
        }
        return null;
    }

    /**
     * Applies placeholders to a message.
     * @param message The message.
     * @param replacements The replacements of the message. Expects input to be even and in a key-value like format.
     *                     Example: ["%player%", "Player"]
     * @return The message with translated placeholders.
     */
    public static String apply(String message, String... replacements) {
        if (message == null || message.isEmpty()) return null;
        message = message.replace("\\n", "\n");
        for (int i = 0; i < replacements.length; i++, i++) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    /**
     * Sends a message to a player with translated placeholders.
     * @param sender The receiver.
     * @param msg The message to be sent.
     * @param replacements The replacements to be taken into account.
     */
    public static void sendRawMessage(CommandSender sender, String msg, String... replacements) {
        String message = apply(msg, replacements);
        if (message != null) {
            sender.sendMessage(color(message));
        }
    }

    /**
     * Sends a message from the specified config path to a player with translated placeholders.
     * @param sender The receiver.
     * @param path The yml path to the message.
     * @param replacements The replacements to be taken into account.
     */
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
