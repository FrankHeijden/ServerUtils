package net.frankheijden.serverutils.common.utils;

import java.nio.charset.StandardCharsets;

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

    /**
     * Joins strings from an array starting from begin index (including).
     * @param delimiter The delimiter to join the strings on.
     * @param strings The string array.
     * @param begin Begin index (including)
     * @return The joined string.
     */
    public static String join(String delimiter, String[] strings, int begin) {
        return join(delimiter, strings, begin, strings.length);
    }

    /**
     * Joins strings from an array from begin index (including) until end index (excluding).
     * @param delimiter The delimiter to join the strings on.
     * @param strings The string array.
     * @param begin Begin index (including)
     * @param end End index (excluding)
     * @return The joined string.
     */
    public static String join(String delimiter, String[] strings, int begin, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < end; i++) {
            sb.append(delimiter).append(strings[i]);
        }
        return sb.substring(1);
    }

    private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    /**
     * Converts a bytes array to hex.
     * via https://stackoverflow.com/a/9855338/11239174
     */
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
