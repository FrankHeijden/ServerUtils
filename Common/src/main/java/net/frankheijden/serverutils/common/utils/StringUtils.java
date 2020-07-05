package net.frankheijden.serverutils.common.utils;

public class StringUtils {

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
}
