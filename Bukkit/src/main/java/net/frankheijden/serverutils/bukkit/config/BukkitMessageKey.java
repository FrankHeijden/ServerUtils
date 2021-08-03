package net.frankheijden.serverutils.bukkit.config;

import java.util.Locale;
import net.frankheijden.serverutils.common.config.PlaceholderConfigKey;

public enum BukkitMessageKey implements PlaceholderConfigKey {

    RELOADCONFIG_SUCCESS("reloadconfig.success"),
    RELOADCONFIG_WARNINGS("reloadconfig.warnings"),
    RELOADCONFIG_NOT_EXISTS("reloadconfig.not-exists"),
    RELOADCONFIG_NOT_SUPPORTED("reloadconfig.not-supported"),
    ENABLEPLUGIN("enableplugin"),
    DISABLEPLUGIN("disableplugin"),
    ;

    private final String path;
    private final boolean hasPlaceholders;

    BukkitMessageKey(String path) {
        this(path, true);
    }

    BukkitMessageKey(String path, boolean hasPlaceholders) {
        this.path = path;
        this.hasPlaceholders = hasPlaceholders;
    }

    public static BukkitMessageKey fromPath(String path) {
        return BukkitMessageKey.valueOf(path.replaceAll("\\.|-", "_").toUpperCase(Locale.ENGLISH));
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean hasPlaceholders() {
        return hasPlaceholders;
    }
}
