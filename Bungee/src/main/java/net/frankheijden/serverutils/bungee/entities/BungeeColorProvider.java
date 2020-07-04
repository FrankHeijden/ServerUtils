package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.providers.ColorProvider;
import net.md_5.bungee.api.ChatColor;

public class BungeeColorProvider implements ColorProvider {

    @Override
    public String apply(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
