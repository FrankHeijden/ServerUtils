package net.frankheijden.serverutils.velocity.managers;

import com.google.common.base.Joiner;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import java.io.Closeable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.common.entities.exceptions.InvalidPluginDescriptionException;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResults;
import net.frankheijden.serverutils.common.entities.results.PluginResults;
import net.frankheijden.serverutils.common.entities.results.Result;
import net.frankheijden.serverutils.common.events.PluginEvent;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.velocity.entities.VelocityPluginDescription;
import net.frankheijden.serverutils.velocity.events.VelocityPluginDisableEvent;
import net.frankheijden.serverutils.velocity.events.VelocityPluginEnableEvent;
import net.frankheijden.serverutils.velocity.events.VelocityPluginLoadEvent;
import net.frankheijden.serverutils.velocity.events.VelocityPluginUnloadEvent;
import net.frankheijden.serverutils.velocity.reflection.RJavaPluginLoader;
import net.frankheijden.serverutils.velocity.reflection.RVelocityCommandManager;
import net.frankheijden.serverutils.velocity.reflection.RVelocityConsole;
import net.frankheijden.serverutils.velocity.reflection.RVelocityEventManager;
import net.frankheijden.serverutils.velocity.reflection.RVelocityPluginContainer;
import net.frankheijden.serverutils.velocity.reflection.RVelocityPluginManager;
import net.frankheijden.serverutils.velocity.reflection.RVelocityScheduler;
import org.slf4j.Logger;

public class VelocityPluginManager extends AbstractPluginManager<PluginContainer, VelocityPluginDescription> {

    private static VelocityPluginManager instance;
    private final ProxyServer proxy;
    private final Logger logger;
    private final VelocityPluginCommandManager pluginCommandManager;

    /**
     * Constructs a new VelocityPluginManager.
     */
    public VelocityPluginManager(ProxyServer proxy, Logger logger, VelocityPluginCommandManager pluginCommandManager) {
        instance = this;
        this.proxy = proxy;
        this.logger = logger;
        this.pluginCommandManager = pluginCommandManager;
    }

    public static VelocityPluginManager get() {
        return instance;
    }

    @Override
    public PluginResults<PluginContainer> loadPluginDescriptions(List<VelocityPluginDescription> descriptions) {
        PluginResults<PluginContainer> loadResults = new PluginResults<>();

        for (VelocityPluginDescription description : descriptions) {
            Path source = description.getFile().toPath();
            Path baseDirectory = source.getParent();

            Object javaPluginLoader = RJavaPluginLoader.newInstance(proxy, baseDirectory);
            PluginDescription candidate = RJavaPluginLoader.loadPluginDescription(javaPluginLoader, source);

            dependencyCheck:
            for (PluginDependency dependency : candidate.getDependencies()) {
                String pluginId = dependency.getId();
                for (VelocityPluginDescription desc : descriptions) {
                    if (desc.getId().equals(pluginId)) continue dependencyCheck;
                }

                if (!dependency.isOptional() && !proxy.getPluginManager().isLoaded(dependency.getId())) {
                    logger.error(
                            "Can't load plugin {} due to missing dependency {}",
                            candidate.getId(),
                            dependency.getId()
                    );
                    return loadResults.addResult(
                            description.getId(),
                            Result.UNKNOWN_DEPENDENCY.arg(dependency.getId())
                    );
                }
            }

            PluginDescription realPlugin = RJavaPluginLoader.loadPlugin(javaPluginLoader, candidate);
            PluginContainer container = RVelocityPluginContainer.newInstance(realPlugin);
            proxy.getEventManager().fire(new VelocityPluginLoadEvent(container, PluginEvent.Stage.PRE));
            proxy.getEventManager().fire(new VelocityPluginLoadEvent(container, PluginEvent.Stage.POST));

            loadResults.addResult(description.getId(), container);
        }

        return loadResults;
    }

    @Override
    public PluginResults<PluginContainer> enableOrderedPlugins(List<PluginContainer> containers) {
        PluginResults<PluginContainer> enableResults = new PluginResults<>();

        List<Object> pluginInstances = new ArrayList<>(containers.size());
        for (PluginContainer container : containers) {
            String pluginId = container.getDescription().getId();
            proxy.getEventManager().fire(new VelocityPluginEnableEvent(container, PluginEvent.Stage.PRE));
            if (isPluginEnabled(pluginId)) {
                return enableResults.addResult(pluginId, Result.ALREADY_ENABLED);
            }

            Object javaPluginLoader = RJavaPluginLoader.newInstance(
                    proxy,
                    container.getDescription().getSource().map(Path::getParent).orElse(null)
            );
            PluginDescription realPlugin = container.getDescription();
            Module module = RJavaPluginLoader.createModule(javaPluginLoader, container);

            AbstractModule commonModule = new AbstractModule() {
                @Override
                protected void configure() {
                    bind(ProxyServer.class).toInstance(proxy);
                    bind(PluginManager.class).toInstance(proxy.getPluginManager());
                    bind(EventManager.class).toInstance(proxy.getEventManager());
                    bind(CommandManager.class).toInstance(proxy.getCommandManager());
                    for (PluginContainer container : proxy.getPluginManager().getPlugins()) {
                        bind(PluginContainer.class)
                                .annotatedWith(Names.named(container.getDescription().getId()))
                                .toInstance(container);
                    }
                    for (PluginContainer container : containers) {
                        bind(PluginContainer.class)
                                .annotatedWith(Names.named(container.getDescription().getId()))
                                .toInstance(container);
                    }
                }
            };

            try {
                RJavaPluginLoader.createPlugin(javaPluginLoader, container, module, commonModule);
            } catch (Exception ex) {
                logger.error(
                        String.format("Can't create plugin %s", container.getDescription().getId()),
                        ex
                );
                return enableResults.addResult(pluginId, Result.ERROR);
            }

            logger.info(
                    "Loaded plugin {} {} by {}",
                    realPlugin.getId(),
                    realPlugin.getVersion().orElse("<UNKNOWN>"),
                    Joiner.on(", ").join(realPlugin.getAuthors())
            );

            RVelocityPluginManager.registerPlugin(proxy.getPluginManager(), container);
            Optional<?> instanceOptional = container.getInstance();
            if (instanceOptional.isPresent()) {
                Object pluginInstance = instanceOptional.get();
                RVelocityEventManager.registerInternally(proxy.getEventManager(), container, pluginInstance);
                pluginInstances.add(pluginInstance);
            }
        }

        RVelocityEventManager.fireForPlugins(
                proxy.getEventManager(),
                new ProxyInitializeEvent(),
                pluginInstances
        ).join();

        ConsoleCommandSource console = proxy.getConsoleCommandSource();
        PermissionsSetupEvent event = new PermissionsSetupEvent(
                console,
                s -> PermissionFunction.ALWAYS_TRUE
        );
        PermissionFunction permissionFunction = RVelocityEventManager.fireForPlugins(
                proxy.getEventManager(),
                event,
                pluginInstances
        ).join().createFunction(console);

        if (permissionFunction == null) {
            logger.error(
                    "A plugin permission provider {} provided an invalid permission function for the console."
                            + " This is a bug in the plugin, not in Velocity."
                            + " Falling back to the default permission function.",
                    event.getProvider().getClass().getName()
            );
            permissionFunction = PermissionFunction.ALWAYS_TRUE;
        }

        RVelocityConsole.setPermissionFunction(console, permissionFunction);

        for (PluginContainer container : containers) {
            proxy.getEventManager().fire(new VelocityPluginEnableEvent(container, PluginEvent.Stage.POST));
            enableResults.addResult(container.getDescription().getId(), container);
        }

        return enableResults;
    }

    @Override
    public boolean isPluginEnabled(String pluginId) {
        return proxy.getPluginManager().isLoaded(pluginId);
    }

    @Override
    public PluginResults<PluginContainer> disableOrderedPlugins(List<PluginContainer> containers) {
        PluginResults<PluginContainer> disableResults = new PluginResults<>();

        List<Object> pluginInstances = new ArrayList<>(containers.size());
        for (PluginContainer container : containers) {
            proxy.getEventManager().fire(new VelocityPluginDisableEvent(container, PluginEvent.Stage.PRE));
            String pluginId = getPluginId(container);
            Object pluginInstance = container.getInstance().orElse(null);
            if (pluginInstance == null) {
                return disableResults.addResult(pluginId, Result.ALREADY_DISABLED);
            }

            pluginInstances.add(pluginInstance);
        }

        RVelocityEventManager.fireForPlugins(
                proxy.getEventManager(),
                new ProxyShutdownEvent(),
                pluginInstances
        );

        for (PluginContainer container : containers) {
            proxy.getEventManager().fire(new VelocityPluginDisableEvent(container, PluginEvent.Stage.POST));
            disableResults.addResult(getPluginId(container), container);
        }

        return disableResults;
    }

    @Override
    public CloseablePluginResults<PluginContainer> unloadOrderedPlugins(List<PluginContainer> containers) {
        CloseablePluginResults<PluginContainer> unloadResults = new CloseablePluginResults<>();

        for (PluginContainer container : containers) {
            proxy.getEventManager().fire(new VelocityPluginUnloadEvent(container, PluginEvent.Stage.PRE));
            String pluginId = getPluginId(container);
            Optional<?> pluginInstanceOptional = container.getInstance();
            if (!pluginInstanceOptional.isPresent()) {
                return unloadResults.addResult(pluginId, Result.INVALID_PLUGIN);
            }

            Object pluginInstance = pluginInstanceOptional.get();

            proxy.getEventManager().unregisterListeners(pluginInstance);
            for (ScheduledTask task : RVelocityScheduler.getTasksByPlugin(proxy.getScheduler())
                    .removeAll(pluginInstance)) {
                task.cancel();
            }

            for (String alias : pluginCommandManager.getPluginCommands().removeAll(pluginId)) {
                proxy.getCommandManager().unregister(alias);
            }

            RVelocityPluginManager.getPlugins(proxy.getPluginManager()).remove(pluginId);
            RVelocityPluginManager.getPluginInstances(proxy.getPluginManager()).remove(pluginInstance);

            List<Closeable> closeables = new ArrayList<>();

            ClassLoader loader = pluginInstance.getClass().getClassLoader();
            if (loader instanceof Closeable) {
                closeables.add((Closeable) loader);
            }

            proxy.getEventManager().fire(new VelocityPluginUnloadEvent(container, PluginEvent.Stage.POST));
            unloadResults.addResult(pluginId, container, closeables);
        }

        return unloadResults;
    }

    @Override
    public List<PluginContainer> getPlugins() {
        return new ArrayList<>(proxy.getPluginManager().getPlugins());
    }

    @Override
    public String getPluginId(PluginContainer plugin) {
        return plugin.getDescription().getId();
    }

    @Override
    public File getPluginFile(PluginContainer plugin) {
        return plugin.getDescription().getSource()
                .map(Path::toFile)
                .orElse(null);
    }

    @Override
    public Optional<File> getPluginFile(String pluginName) {
        Object javaPluginLoader = RJavaPluginLoader.newInstance(instance.proxy, getPluginsFolder().toPath());

        for (File file : getPluginJars()) {
            PluginDescription desc = RJavaPluginLoader.loadPluginDescription(javaPluginLoader, file.toPath());

            if (desc.getId().equals(pluginName)) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<PluginContainer> getPlugin(String pluginName) {
        return proxy.getPluginManager().getPlugin(pluginName);
    }

    @Override
    public VelocityPluginDescription getLoadedPluginDescription(PluginContainer plugin) {
        return new VelocityPluginDescription(plugin.getDescription());
    }

    @Override
    public Optional<VelocityPluginDescription> getPluginDescription(
            File file
    ) throws InvalidPluginDescriptionException {
        Path source = file.toPath();
        Path baseDirectory = source.getParent();

        try {
            Object javaPluginLoader = RJavaPluginLoader.newInstance(proxy, baseDirectory);
            PluginDescription candidate = RJavaPluginLoader.loadPluginDescription(javaPluginLoader, source);
            return Optional.of(new VelocityPluginDescription(candidate));
        } catch (Exception ex) {
            throw new InvalidPluginDescriptionException(ex);
        }
    }

    @Override
    public Object getInstance(PluginContainer plugin) {
        return plugin.getInstance().orElse(null);
    }

    @Override
    public Set<String> getCommands() {
        return RVelocityCommandManager.getDispatcher(proxy.getCommandManager()).getRoot().getChildren().stream()
                .map(CommandNode::getName)
                .collect(Collectors.toSet());
    }
}
