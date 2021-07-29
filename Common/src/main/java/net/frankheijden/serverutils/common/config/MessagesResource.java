package net.frankheijden.serverutils.common.config;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.utils.StringUtils;

public class MessagesResource extends ServerUtilsResource {

    public static final String MESSAGES_RESOURCE = "messages";

    public MessagesResource(ServerUtilsPlugin<?, ?, ?, ?, ?> plugin) {
        super(plugin, MESSAGES_RESOURCE);
    }

    /**
     * Retrieves a message from the config.
     * @param path The yml path to the message.
     * @param replacements The replacements to be taken into account.
     * @return The config message with translated placeholders.
     */
    public String getMessage(String path, String... replacements) {
        String message = config.getString(path);
        if (message != null) {
            return StringUtils.apply(message, replacements);
        } else {
            plugin.getLogger().severe("Missing locale in messages.yml at path '" + path + "'!");
        }
        return null;
    }

    /**
     * Sends a message to a player with translated placeholders.
     * @param sender The receiver.
     * @param msg The message to be sent.
     * @param replacements The replacements to be taken into account.
     */
    public void sendRawMessage(ServerCommandSender<?> sender, String msg, String... replacements) {
        String message = StringUtils.apply(msg, replacements);
        if (message != null) {
            sender.sendMessage(plugin.getChatProvider().color(message));
        }
    }

    /**
     * Sends a message from the specified config path to a player with translated placeholders.
     * @param sender The receiver.
     * @param path The yml path to the message.
     * @param replacements The replacements to be taken into account.
     */
    public void sendMessage(ServerCommandSender<?> sender, String path, String... replacements) {
        String message = getMessage(path, replacements);
        if (message != null) {
            sender.sendMessage(plugin.getChatProvider().color(message));
        }
    }

    @Override
    public void migrate(int currentConfigVersion) {
        if (currentConfigVersion <= 1) {
            reset("serverutils.help.format");
        }
    }
}
