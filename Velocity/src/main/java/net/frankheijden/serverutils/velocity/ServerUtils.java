package net.frankheijden.serverutils.velocity;

import co.aikar.commands.CommandCompletions;
import co.aikar.commands.VelocityCommandCompletionContext;
import co.aikar.commands.VelocityCommandManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.velocity.commands.CommandPlugins;
import net.frankheijden.serverutils.velocity.commands.CommandServerUtils;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginManager;
import net.frankheijden.serverutils.velocity.reflection.RVelocityCommandManager;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

@Plugin(
        id = "serverutils",
        name = "ServerUtils",
        version = "${version}",
        description = "A server utility",
        url = "https://github.com/FrankHeijden/ServerUtils",
        authors = "FrankHeijden"
)
public class ServerUtils {

    private static ServerUtils instance;
    private static final String CONFIG_RESOURCE = "velocity-config.toml";
    private static final String MESSAGES_RESOURCE = "velocity-messages.toml";

    private VelocityPlugin plugin;
    private VelocityCommandManager commandManager;

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

    private final Multimap<String, String> pluginCommands = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    /**
     * Initialises ServerUtils.
     */
    @Inject
    public ServerUtils(ProxyServer proxy) {
        RVelocityCommandManager.proxyRegistrars(
                proxy,
                getClass().getClassLoader(),
                (container, meta) -> pluginCommands.putAll(container.getDescription().getId(), meta.getAliases())
        );
    }

    /**
     * Initialises and enables ServerUtils.
     */
    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        instance = this;

        this.plugin = new VelocityPlugin(this);
        ServerUtilsApp.init(this, plugin);

        metricsFactory.make(this, ServerUtilsApp.BSTATS_METRICS_ID);

        this.commandManager = new VelocityCommandManager(proxy, this);
        commandManager.registerCommand(new CommandPlugins());
        commandManager.registerCommand(new CommandServerUtils(this));

        VelocityPluginManager manager = plugin.getPluginManager();
        CommandCompletions<VelocityCommandCompletionContext> completions = commandManager.getCommandCompletions();
        completions.registerAsyncCompletion("plugins", context -> manager.getPluginNames());
        completions.registerAsyncCompletion("pluginJars", context -> manager.getPluginFileNames());
        completions.registerAsyncCompletion("commands", context -> manager.getCommands());

        reload();
        plugin.enable();

        ServerUtilsApp.tryCheckForUpdates();
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

    public VelocityCommandManager getCommandManager() {
        return commandManager;
    }

    public VelocityPlugin getPlugin() {
        return plugin;
    }

    public Multimap<String, String> getPluginCommands() {
        return pluginCommands;
    }

    public void reload() {
        new Config("config.toml", CONFIG_RESOURCE);
        new Messenger("messages.toml", MESSAGES_RESOURCE);
    }
}
