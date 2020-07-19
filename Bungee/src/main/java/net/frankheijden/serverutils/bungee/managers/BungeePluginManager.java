package net.frankheijden.serverutils.bungee.managers;

import com.google.common.base.Preconditions;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeeLoadResult;
import net.frankheijden.serverutils.bungee.reflection.RPluginClassLoader;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.yaml.snakeyaml.Yaml;

public class BungeePluginManager extends AbstractPluginManager<Plugin> {

    private static final ProxyServer proxy = ProxyServer.getInstance();
    private static final ServerUtils plugin = ServerUtils.getInstance();

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
    public BungeeLoadResult loadPlugin(String pluginFile) {
        File file = getPluginFileExact(pluginFile);
        if (!file.exists()) return new BungeeLoadResult(Result.NOT_EXISTS);
        return loadPlugin(file);
    }

    @Override
    public BungeeLoadResult loadPlugin(File file) {
        PluginDescription desc;
        try {
            desc = getPluginDescription(file);
        } catch (Exception ex) {
            proxy.getLogger().log(Level.WARNING, "Could not load plugin from file " + file, ex);
            return new BungeeLoadResult(Result.INVALID_DESCRIPTION);
        }

        try {
            URL url = desc.getFile().toURI().toURL();
            URLClassLoader loader = (URLClassLoader) RPluginClassLoader.newInstance(proxy, desc, url);

            Class<?> main = loader.loadClass(desc.getMain());
            Plugin plugin = (Plugin) main.getDeclaredConstructor().newInstance();

            RPluginManager.getPlugins(proxy.getPluginManager()).put(desc.getName(), plugin);
            plugin.onLoad();
            proxy.getLogger().log(Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[] {
                    desc.getName(), desc.getVersion(), desc.getAuthor()
            });
            return new BungeeLoadResult(plugin);
        } catch (Throwable th) {
            proxy.getLogger().log(Level.WARNING, "Error loading plugin " + desc.getName(), th);
            return new BungeeLoadResult(Result.ERROR);
        }
    }

    @Override
    public Result enablePlugin(Plugin plugin) {
        PluginDescription desc = plugin.getDescription();
        String name = desc.getName();
        try {
            plugin.onEnable();
            Object[] args = new Object[] { name, desc.getVersion(), desc.getAuthor() };
            proxy.getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", args);
            return Result.SUCCESS;
        } catch (Throwable th) {
            proxy.getLogger().log(Level.WARNING, "Exception encountered when loading plugin: " + name, th);
            return Result.ERROR;
        }
    }

    @Override
    public Result reloadPlugin(String pluginName) {
        Plugin plugin = proxy.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return Result.NOT_ENABLED;
        return reloadPlugin(plugin);
    }

    @Override
    public Result reloadPlugin(Plugin plugin) {
        CloseableResult result = unloadPlugin(plugin);
        if (result.getResult() != Result.SUCCESS) return result.getResult();
        result.tryClose();

        File file = getPluginFile(plugin.getDescription().getName());
        if (file == null) return Result.FILE_DELETED;

        BungeeLoadResult loadResult = loadPlugin(file);
        if (!loadResult.isSuccess()) return loadResult.getResult();

        return enablePlugin(loadResult.get());
    }

    @Override
    public CloseableResult unloadPlugin(String pluginName) {
        Plugin plugin = proxy.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return new CloseableResult(Result.NOT_ENABLED);
        return unloadPlugin(plugin);
    }

    @Override
    public CloseableResult unloadPlugin(Plugin plugin) {
        plugin.onDisable();
        proxy.getPluginManager().unregisterCommands(plugin);
        proxy.getPluginManager().unregisterListeners(plugin);
        proxy.getScheduler().cancel(plugin);
        Closeable closeable;
        try {
            RPluginManager.clearPlugin(proxy.getPluginManager(), plugin);
            closeable = (Closeable) RPluginClassLoader.getPluginClassLoader(plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new CloseableResult(Result.ERROR);
        }
        return new CloseableResult(closeable);
    }

    public static File getPluginFileExact(String fileName) {
        return new File(proxy.getPluginsFolder(), fileName);
    }

    /**
     * Retrieves the File of a plugin associated with a name.
     * @param pluginName The plugin name to search for.
     * @return The File if the plugin exists with that name.
     */
    public File getPluginFile(String pluginName) {
        for (File file : getPluginJars()) {
            PluginDescription desc;
            try {
                desc = getPluginDescription(file);
            } catch (Exception ex) {
                continue;
            }

            if (desc.getName().equals(pluginName)) return file;
        }
        return null;
    }

    @Override
    public File getPluginFile(Plugin plugin) {
        return plugin.getFile();
    }

    @Override
    public Set<String> getCommands() {
        return proxy.getPluginManager().getCommands().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the PluginDescription of a (plugin's) File.
     * @param file The file.
     * @return The PluginDescription.
     * @throws Exception Iff and I/O exception occurred, or notNullChecks failed.
     */
    public static PluginDescription getPluginDescription(File file) throws Exception {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = getPluginDescriptionEntry(jar);
            Preconditions.checkNotNull(entry, "Plugin must have a plugin.yml or bungee.yml");

            try (InputStream in = jar.getInputStream(entry)) {
                Yaml yaml = RPluginManager.getYaml(proxy.getPluginManager());
                PluginDescription desc = yaml.loadAs(in, PluginDescription.class);
                Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
                Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);

                desc.setFile(file);
                return desc;
            }
        }
    }

    /**
     * Retrieves the JarEntry which contains the Description file of the JarFile.
     * @param jar The JarFile.
     * @return The description JarEntry.
     */
    public static JarEntry getPluginDescriptionEntry(JarFile jar) {
        JarEntry entry = jar.getJarEntry("bungee.yml");
        if (entry == null) return jar.getJarEntry("plugin.yml");
        return entry;
    }

    @Override
    public File getPluginsFolder() {
        return plugin.getProxy().getPluginsFolder();
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
        Collection<Plugin> plugins = plugin.getProxy().getPluginManager().getPlugins();
        if (modules) return new ArrayList<>(plugins);
        return plugins.stream()
                .filter(BungeePluginManager::isPlugin)
                .collect(Collectors.toList());
    }

    @Override
    public String getPluginName(Plugin plugin) {
        return plugin.getDataFolder().getName();
    }

    /**
     * Retrieves the plugins sorted by their names.
     * @param modules Whether or not to include `module` plugins
     * @return The sorted plugins.
     */
    public List<Plugin> getPluginsSorted(boolean modules) {
        List<Plugin> plugins = getPlugins(modules);
        plugins.sort(Comparator.comparing(this::getPluginName));
        return plugins;
    }
}
