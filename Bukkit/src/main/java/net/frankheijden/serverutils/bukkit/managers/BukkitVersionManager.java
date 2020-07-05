package net.frankheijden.serverutils.bukkit.managers;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.managers.AbstractVersionManager;

public class BukkitVersionManager extends AbstractVersionManager {

    /**
     * Creates a new VersionManager instance.
     * Used for automatic updating.
     */
    public BukkitVersionManager(ServerUtils plugin) {
        super(plugin.getDescription().getVersion());
    }
}
