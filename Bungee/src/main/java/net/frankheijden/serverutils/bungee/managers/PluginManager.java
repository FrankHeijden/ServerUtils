package net.frankheijden.serverutils.bungee.managers;

import com.google.common.base.Preconditions;
import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeeLoadResult;
import net.frankheijden.serverutils.bungee.entities.BungeePluginProvider;
import net.frankheijden.serverutils.bungee.reflection.RPluginClassLoader;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.yaml.snakeyaml.Yaml;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class PluginManager {

    private static final ProxyServer proxy = ProxyServer.getInstance();
    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static final BungeePluginProvider provider = (BungeePluginProvider) plugin.getPlugin().getPluginProvider();

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

    public static BungeeLoadResult loadPlugin(String fileName) {
        File file = getPluginFileExact(fileName);
        if (!file.exists()) return new BungeeLoadResult(Result.NOT_EXISTS);
        return loadPlugin(file);
    }

    public static BungeeLoadResult loadPlugin(File file) {
        PluginDescription desc;
        try {
            desc = getPluginDescription(file);
        } catch (Exception ex) {
            proxy.getLogger().log(Level.WARNING, "Could not load plugin from file " + file, ex );
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

    public static Result enablePlugin(Plugin plugin) {
        PluginDescription desc = plugin.getDescription();
        String name = desc.getName();
        try {
            plugin.onEnable();
            proxy.getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", new Object[] {
                    name, desc.getVersion(), desc.getAuthor()
            });
            return Result.SUCCESS;
        } catch (Throwable th) {
            proxy.getLogger().log(Level.WARNING, "Exception encountered when loading plugin: " + name, th);
            return Result.ERROR;
        }
    }

    public static CloseableResult reloadPlugin(String pluginName) {
        Plugin plugin = proxy.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return new CloseableResult(Result.NOT_ENABLED);
        return reloadPlugin(plugin);
    }

    public static CloseableResult reloadPlugin(Plugin plugin) {
        CloseableResult result = unloadPlugin(plugin);
        if (result.getResult() != Result.SUCCESS) return result;

        File file = getPluginFile(plugin.getDescription().getName());
        if (file == null) return result.set(Result.FILE_DELETED);

        BungeeLoadResult loadResult = loadPlugin(file);
        if (!loadResult.isSuccess()) return result.set(loadResult.getResult());

        return result.set(enablePlugin(loadResult.get()));
    }

    public static CloseableResult unloadPlugin(String pluginName) {
        Plugin plugin = proxy.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return new CloseableResult(Result.NOT_ENABLED);
        return unloadPlugin(plugin);
    }

    public static CloseableResult unloadPlugin(Plugin plugin) {
        plugin.onDisable();
        proxy.getPluginManager().unregisterCommands(plugin);
        proxy.getPluginManager().unregisterListeners(plugin);
        try {
            RPluginManager.clearPlugin(proxy.getPluginManager(), plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new CloseableResult(Result.ERROR);
        }
        return new CloseableResult(getCloseable(plugin));
    }

    public static File getPluginFileExact(String fileName) {
        return new File(proxy.getPluginsFolder(), fileName);
    }

    public static File getPluginFile(String pluginName) {
        for (File file : provider.getPluginJars()) {
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

    public static JarEntry getPluginDescriptionEntry(JarFile jar) {
        JarEntry entry = jar.getJarEntry("bungee.yml");
        if (entry == null) return jar.getJarEntry("plugin.yml");
        return entry;
    }

    public static Closeable getCloseable(Plugin plugin) {
        ClassLoader loader = plugin.getClass().getClassLoader();
        if (loader instanceof Closeable) return (Closeable) loader;
        return null;
    }
}
