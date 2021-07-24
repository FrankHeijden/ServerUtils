package net.frankheijden.serverutils.common.config;

import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class ConfigResource extends ServerUtilsResource {

    private static final String CONFIG_RESOURCE = "config";

    public ConfigResource(ServerUtilsPlugin<?, ?, ?, ?> plugin) {
        super(plugin, CONFIG_RESOURCE);
    }
}
