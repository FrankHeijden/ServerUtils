package net.frankheijden.serverutils.bungee.managers;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.common.managers.AbstractVersionManager;

public class BungeeVersionManager extends AbstractVersionManager {

    /**
     * Creates a new VersionManager instance.
     * Used for automatic updating.
     */
    public BungeeVersionManager(ServerUtils plugin) {
        super(plugin.getDescription().getVersion());
    }
}
