package net.frankheijden.serverutils.bungee.managers;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.bungee.entities.BungeePluginDescription;
import net.frankheijden.serverutils.bungee.events.BungeePluginDisableEvent;
import net.frankheijden.serverutils.bungee.events.BungeePluginEnableEvent;
import net.frankheijden.serverutils.bungee.events.BungeePluginLoadEvent;
import net.frankheijden.serverutils.bungee.events.BungeePluginUnloadEvent;
import net.frankheijden.serverutils.bungee.reflection.RLibraryLoader;
import net.frankheijden.serverutils.bungee.reflection.RPluginClassLoader;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.entities.exceptions.InvalidPluginDescriptionException;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResults;
import net.frankheijden.serverutils.common.entities.results.PluginResults;
import net.frankheijden.serverutils.common.entities.results.Result;
import net.frankheijden.serverutils.common.events.PluginEvent;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.yaml.snakeyaml.Yaml;

public class BungeePluginManager extends AbstractPluginManager<Plugin, BungeePluginDescription> {

    private static final ProxyServer proxy = ProxyServer.getInstance();

    private static BungeePluginManager instance;

    public BungeePluginManager() {
        instance = this;
    }

    public static BungeePluginManager get() {
        return instance;
    }

    /**
     * Checks whether a loaded plugin is a module.
     * @param plugin The plugin to check.
     * @return Whether or not it's a module.
     */
    public static boolean isModule(Plugin plugin) {
        return plugin.getFile().getParent().equalsIgnoreCase("modules");
    }

    /**
     * Checks whether a loaded plugin is an actual plugin and not a module.
     * @param plugin The plugin to check.
     * @return Whether or not it's a plugin.
     */
    public static boolean isPlugin(Plugin plugin) {
        return !isModule(plugin);
    }

    @Override
    public PluginResults<Plugin> loadPluginDescriptions(List<BungeePluginDescription> descriptions) {
        PluginResults<Plugin> loadResults = new PluginResults<>();

        for (BungeePluginDescription description : descriptions) {
            PluginDescription desc = description.getDescription();
            Plugin plugin;

            try {
                Object libraryLoader = RPluginManager.getLibraryLoader(proxy.getPluginManager());
                ClassLoader classLoader = RLibraryLoader.createLoader(libraryLoader, desc);
                URLClassLoader loader = (URLClassLoader) RPluginClassLoader.newInstance(
                        proxy,
                        desc,
                        desc.getFile(),
                        classLoader
                );

                Class<?> main = loader.loadClass(desc.getMain());
                plugin = (Plugin) main.getDeclaredConstructor().newInstance();

                RPluginManager.getPlugins(proxy.getPluginManager()).put(description.getId(), plugin);
                proxy.getPluginManager().callEvent(new BungeePluginLoadEvent(plugin, PluginEvent.Stage.PRE));
                plugin.onLoad();
            } catch (Throwable th) {
                proxy.getLogger().log(Level.WARNING, "Error loading plugin " + description.getId(), th);
                return loadResults.addResult(description.getId(), Result.ERROR);
            }

            proxy.getLogger().log(Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[] {
                    desc.getName(), desc.getVersion(), desc.getAuthor()
            });
            proxy.getPluginManager().callEvent(new BungeePluginLoadEvent(plugin, PluginEvent.Stage.POST));
            loadResults.addResult(description.getId(), plugin);
        }

        return loadResults;
    }

    @Override
    public PluginResults<Plugin> enableOrderedPlugins(List<Plugin> plugins) {
        PluginResults<Plugin> enableResults = new PluginResults<>();

        for (Plugin plugin : plugins) {
            ServerUtilsPluginDescription description = getLoadedPluginDescription(plugin);
            String pluginId = description.getId();

            proxy.getPluginManager().callEvent(new BungeePluginEnableEvent(plugin, PluginEvent.Stage.PRE));
            try {
                plugin.onEnable();
            } catch (Throwable th) {
                proxy.getLogger().log(Level.WARNING, "Exception encountered when loading plugin: " + pluginId, th);
                return enableResults.addResult(pluginId, Result.ERROR);
            }

            Object[] args = new Object[] { description.getId(), description.getVersion(), description.getAuthor() };
            proxy.getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", args);
            proxy.getPluginManager().callEvent(new BungeePluginEnableEvent(plugin, PluginEvent.Stage.POST));
            enableResults.addResult(pluginId, plugin);
        }

        return enableResults;
    }

    @Override
    public boolean isPluginEnabled(String pluginId) {
        return proxy.getPluginManager().getPlugin(pluginId) != null;
    }

    @Override
    public PluginResults<Plugin> disableOrderedPlugins(List<Plugin> plugins) {
        PluginResults<Plugin> disableResults = new PluginResults<>();

        for (Plugin plugin : plugins) {
            String pluginId = getPluginId(plugin);
            proxy.getPluginManager().callEvent(new BungeePluginDisableEvent(plugin, PluginEvent.Stage.PRE));
            try {
                plugin.onDisable();
            } catch (Throwable th) {
                proxy.getLogger().log(Level.WARNING, "Exception encountered when disabling plugin: " + pluginId, th);
                return disableResults.addResult(pluginId, Result.ERROR);
            }

            proxy.getPluginManager().callEvent(new BungeePluginDisableEvent(plugin, PluginEvent.Stage.POST));
            disableResults.addResult(pluginId, plugin);
        }

        return disableResults;
    }

    @Override
    public CloseablePluginResults<Plugin> unloadOrderedPlugins(List<Plugin> plugins) {
        CloseablePluginResults<Plugin> unloadResults = new CloseablePluginResults<>();

        for (Plugin plugin : plugins) {
            String pluginId = getPluginId(plugin);

            proxy.getPluginManager().callEvent(new BungeePluginUnloadEvent(plugin, PluginEvent.Stage.PRE));
            plugin.onDisable();
            proxy.getPluginManager().unregisterCommands(plugin);
            proxy.getPluginManager().unregisterListeners(plugin);
            proxy.getScheduler().cancel(plugin);

            List<Closeable> closeables = new ArrayList<>();
            try {
                RPluginManager.clearPlugin(proxy.getPluginManager(), plugin);
                addIfInstance(closeables, RPluginClassLoader.getPluginClassLoader(plugin));
                addIfInstance(closeables, plugin.getClass().getClassLoader());
            } catch (Exception ex) {
                ex.printStackTrace();
                return unloadResults.addResult(pluginId, Result.ERROR);
            }

            proxy.getPluginManager().callEvent(new BungeePluginUnloadEvent(plugin, PluginEvent.Stage.POST));
            unloadResults.addResult(pluginId, plugin, closeables);
        }

        return unloadResults;
    }

    private static void addIfInstance(List<Closeable> list, Object obj) {
        if (obj instanceof Closeable) {
            list.add((Closeable) obj);
        }
    }

    @Override
    public Optional<File> getPluginFile(String pluginId) {
        for (File file : getPluginJars()) {
            BungeePluginDescription description;
            try {
                Optional<BungeePluginDescription> descriptionOptional = getPluginDescription(file);
                if (!descriptionOptional.isPresent()) continue;
                description = descriptionOptional.get();
            } catch (Exception ex) {
                continue;
            }

            if (description.getId().equals(pluginId)) return Optional.of(file);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Plugin> getPlugin(String pluginName) {
        return Optional.ofNullable(proxy.getPluginManager().getPlugin(pluginName));
    }

    @Override
    public BungeePluginDescription getLoadedPluginDescription(Plugin plugin) {
        return new BungeePluginDescription(plugin.getDescription());
    }

    @Override
    public Plugin getInstance(Plugin plugin) {
        return plugin;
    }

    @Override
    public Set<String> getCommands() {
        return proxy.getPluginManager().getCommands().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<BungeePluginDescription> getPluginDescription(File file) throws InvalidPluginDescriptionException {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("bungee.yml");
            if (entry == null) {
                entry = jar.getJarEntry("plugin.yml");
            }

            if (entry == null) {
                throw new InvalidPluginDescriptionException("Plugin must have a plugin.yml or bungee.yml");
            }

            try (InputStream in = jar.getInputStream(entry)) {
                Yaml yaml = RPluginManager.getYaml(proxy.getPluginManager());
                PluginDescription description = yaml.loadAs(in, PluginDescription.class);
                if (description.getName() == null) {
                    throw new InvalidPluginDescriptionException("Plugin from " + file + " has no name");
                } else if (description.getMain() == null) {
                    throw new InvalidPluginDescriptionException("Plugin from " + file + " has no main");
                }

                description.setFile(file);
                return Optional.of(new BungeePluginDescription(description));
            }
        } catch (IOException ex) {
            throw new InvalidPluginDescriptionException(ex);
        }
    }

    @Override
    public File getPluginsFolder() {
        return proxy.getPluginsFolder();
    }

    @Override
    public List<Plugin> getPlugins() {
        return getPlugins(false);
    }

    /**
     * Retrieves a list of plugins.
     * @param modules Whether or not to include `module` plugins.
     * @return The list of plugins.
     */
    public List<Plugin> getPlugins(boolean modules) {
        Collection<Plugin> plugins = proxy.getPluginManager().getPlugins();
        if (modules) return new ArrayList<>(plugins);
        return plugins.stream()
                .filter(BungeePluginManager::isPlugin)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the plugins sorted by their names.
     * @param modules Whether or not to include `module` plugins
     * @return The sorted plugins.
     */
    public List<Plugin> getPluginsSorted(boolean modules) {
        List<Plugin> plugins = getPlugins(modules);
        plugins.sort(Comparator.comparing(this::getPluginId));
        return plugins;
    }
}
