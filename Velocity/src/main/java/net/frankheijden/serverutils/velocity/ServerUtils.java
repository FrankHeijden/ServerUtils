package net.frankheijden.serverutils.velocity;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.nio.file.Path;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginCommandManager;
import net.frankheijden.serverutils.velocity.reflection.RVelocityCommandManager;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

@Plugin(
        id = "serverutils",
        name = "ServerUtils",
        version = "{version}",
        description = "A server utility",
        url = "https://github.com/FrankHeijden/ServerUtils",
        authors = "FrankHeijden"
)
public class ServerUtils {

    private static ServerUtils instance;
    private static final String PLUGIN_COMMANDS_CACHE = ".pluginCommandsCache.json";

    private VelocityPlugin plugin;

    @Inject
    private ProxyServer proxy;

    @Inject
    private Logger logger;

    @Inject
    @DataDirectory
    private Path dataDirectory;

    @Inject
    private Metrics.Factory metricsFactory;

    @Inject
    @Named("serverutils")
    private PluginContainer pluginContainer;

    private final VelocityPluginCommandManager pluginCommandManager;

    /**
     * Initialises ServerUtils.
     */
    @Inject
    public ServerUtils(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        instance = this;
        try {
            this.pluginCommandManager = VelocityPluginCommandManager.load(dataDirectory.resolve(PLUGIN_COMMANDS_CACHE));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        RVelocityCommandManager.proxyRegistrars(
                proxy,
                getClass().getClassLoader(),
                (container, meta) -> pluginCommandManager.getPluginCommands().putAll(
                        container.getDescription().getId(),
                        meta.getAliases()
                )
        );
    }

    /**
     * Initialises and enables ServerUtils.
     */
    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        this.plugin = new VelocityPlugin(this);
        ServerUtilsApp.init(this, plugin);

        metricsFactory.make(this, ServerUtilsApp.BSTATS_METRICS_ID);
        plugin.enable();
    }

    /**
     * De-initialises and disables ServerUtils.
     */
    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        try {
            pluginCommandManager.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ServerUtils getInstance() {
        return instance;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public VelocityPlugin getPlugin() {
        return plugin;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public VelocityPluginCommandManager getPluginCommandManager() {
        return pluginCommandManager;
    }
}
