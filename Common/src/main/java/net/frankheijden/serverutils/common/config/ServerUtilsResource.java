package net.frankheijden.serverutils.common.config;

import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class ServerUtilsResource {

    protected final ServerUtilsPlugin<?, ?, ?, ?> plugin;
    protected final ServerUtilsConfig config;

    protected ServerUtilsResource(ServerUtilsPlugin<?, ?, ?, ?> plugin, ServerUtilsConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    protected ServerUtilsResource(ServerUtilsPlugin<?, ?, ?, ?> plugin, String resourceName) {
        this(
                plugin,
                ServerUtilsConfig.load(
                        plugin,
                        plugin.getDataFolder().toPath().resolve(
                                resourceName + plugin.getResourceProvider().getResourceExtension()
                        ),
                        resourceName
                )
        );
    }

    public ServerUtilsConfig getConfig() {
        return config;
    }
}
