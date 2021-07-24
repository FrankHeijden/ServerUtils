package net.frankheijden.serverutils.common.config;

import java.io.IOException;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public abstract class ServerUtilsResource {

    protected final ServerUtilsPlugin<?, ?, ?, ?> plugin;
    protected final ServerUtilsConfig config;
    protected final JsonConfig defaultConfig;

    protected ServerUtilsResource(
            ServerUtilsPlugin<?, ?, ?, ?> plugin,
            ServerUtilsConfig config,
            JsonConfig defaultConfig
    ) {
        this.plugin = plugin;
        this.config = config;
        this.defaultConfig = defaultConfig;
    }

    protected ServerUtilsResource(ServerUtilsPlugin<?, ?, ?, ?> plugin, String resourceName) {
        this.plugin = plugin;
        this.defaultConfig = JsonConfig.load(plugin.getResourceProvider(), plugin.getPlatform(), resourceName);
        this.config = ServerUtilsConfig.init(
                this.defaultConfig,
                plugin.getResourceProvider(),
                plugin.getDataFolder().toPath().resolve(
                        resourceName + plugin.getResourceProvider().getResourceExtension()
                )
        );
        this.migrate();
    }

    public ServerUtilsConfig getConfig() {
        return config;
    }

    public ServerUtilsConfig getDefaultConfig() {
        return defaultConfig;
    }

    protected void reset(String path) {
        config.set(path, JsonConfig.toObjectValue(defaultConfig.getJsonElement(path)));
    }

    /**
     * Migrates values in the config.
     */
    public void migrate() {
        migrate(config.getInt("config-version"));
        config.set("config-version", defaultConfig.getInt("config-version"));
        try {
            config.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public abstract void migrate(int currentConfigVersion);
}
