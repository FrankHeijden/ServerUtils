package net.frankheijden.serverutils.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides basic conversion between the hex format <#FFFFFF> and bukkit hex format &x&F&F&F&F&F&F.
 */
public class HexUtils {

    private static final Pattern hexPattern = Pattern.compile("<(#[0-9a-fA-F]{6})>");
    private static final char COLOR_CHAR = '&';

    /**
     * Prefixes each character provided with the color character {@link #COLOR_CHAR}.
     * @param color The color to prefix with the color character.
     * @return The prefixed color.
     */
    public static String convertHexColor(String color) {
        StringBuilder sb = new StringBuilder(2 * (color.length() + 1)).append(COLOR_CHAR).append("x");
        for (char c : color.toCharArray()) {
            sb.append(COLOR_CHAR).append(c);
        }
        return sb.toString();
    }

    /**
     * Converts the input string to a bukkit readable color string.
     * The accepted hex format is `<#FFFFFF>`.
     * @param str The input string.
     * @return The output converted hex string.
     */
    public static String convertHexString(String str) {
        StringBuffer sb = new StringBuffer(str.length());
        Matcher matcher = hexPattern.matcher(str);
        while (matcher.find()) {
            String hex = matcher.group();
            matcher.appendReplacement(sb, convertHexColor(hex.substring(2, hex.length() - 1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
