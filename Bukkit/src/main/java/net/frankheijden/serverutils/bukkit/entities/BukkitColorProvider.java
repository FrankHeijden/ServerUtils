package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.providers.ColorProvider;
import net.md_5.bungee.api.ChatColor;

public class BukkitColorProvider implements ColorProvider {

    @Override
    public String apply(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
