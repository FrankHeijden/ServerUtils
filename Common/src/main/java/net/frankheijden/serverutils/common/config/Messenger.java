package net.frankheijden.serverutils.common.config;

import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.utils.StringUtils;

/**
 * The general common messenger class.
 */
public class Messenger extends ServerUtilsResource {

    private static Messenger instance;
    private static final ServerUtilsPlugin plugin = ServerUtilsApp.getPlugin();

    /**
     * Constructs a new Messenger with the messages file name and the resource name from the jar.
     * @param fileName The file name in the data folder.
     * @param resource The resource name in the jar file.
     */
    public Messenger(String fileName, String resource) {
        super(fileName, resource);
        instance = this;
    }

    /**
     * Retrieves the current instance of the Messenger.
     * @return The current instance.
     */
    public static Messenger getInstance() {
        return instance;
    }

    /**
     * Retrieves a message from the config.
     * @param path The yml path to the message.
     * @param replacements The replacements to be taken into account.
     * @return The config message with translated placeholders.
     */
    public static String getMessage(String path, String... replacements) {
        String message = instance.getConfig().getString(path);
        if (message != null) {
            return StringUtils.apply(message, replacements);
        } else {
            Messenger.plugin.getLogger().severe("Missing locale in messages.yml at path '" + path + "'!");
        }
        return null;
    }

    /**
     * Sends a message to a player with translated placeholders.
     * @param sender The receiver.
     * @param msg The message to be sent.
     * @param replacements The replacements to be taken into account.
     */
    public static void sendRawMessage(ServerCommandSender sender, String msg, String... replacements) {
        String message = StringUtils.apply(msg, replacements);
        if (message != null) {
            sender.sendMessage(Messenger.plugin.getChatProvider().color(message));
        }
    }

    /**
     * Sends a message from the specified config path to a player with translated placeholders.
     * @param sender The receiver.
     * @param path The yml path to the message.
     * @param replacements The replacements to be taken into account.
     */
    public static void sendMessage(ServerCommandSender sender, String path, String... replacements) {
        String message = getMessage(path, replacements);
        if (message != null) {
            sender.sendMessage(Messenger.plugin.getChatProvider().color(message));
        }
    }

    /**
     * Colorizes the given string.
     * @param str The string to color.
     * @return The colored string.
     */
    public static String color(String str) {
        return Messenger.plugin.getChatProvider().color(str);
    }
}
