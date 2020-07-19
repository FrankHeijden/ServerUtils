package net.frankheijden.serverutils.bukkit.managers;

import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.bukkit.entities.BukkitLoadResult;
import net.frankheijden.serverutils.bukkit.reflection.RCommandMap;
import net.frankheijden.serverutils.bukkit.reflection.RCraftServer;
import net.frankheijden.serverutils.bukkit.reflection.RCraftingManager;
import net.frankheijden.serverutils.bukkit.reflection.RJavaPlugin;
import net.frankheijden.serverutils.bukkit.reflection.RPluginClassLoader;
import net.frankheijden.serverutils.bukkit.reflection.RSimplePluginManager;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.UnknownDependencyException;

public class BukkitPluginManager extends AbstractPluginManager<Plugin> {

    private final ServerUtils plugin;
    private static BukkitPluginManager instance;

    public BukkitPluginManager(ServerUtils plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static BukkitPluginManager get() {
        return instance;
    }

    /**
     * Loads the specified file as a plugin.
     * @param jarFile The name of the file in the plugins/ folder.
     * @return The result of the loading procedure.
     */
    @Override
    public BukkitLoadResult loadPlugin(String jarFile) {
        return loadPlugin(new File(ServerUtils.getInstance().getDataFolder().getParent(), jarFile));
    }

    /**
     * Loads the specified file as a plugin.
     * @param file The file to be loaded.
     * @return The result of the loading procedure.
     */
    @Override
    public BukkitLoadResult loadPlugin(File file) {
        if (!file.exists()) return new BukkitLoadResult(Result.NOT_EXISTS);

        Plugin loadedPlugin = getLoadedPlugin(file);
        if (loadedPlugin != null) return new BukkitLoadResult(Result.ALREADY_LOADED);

        Plugin plugin;
        try {
            plugin = Bukkit.getPluginManager().loadPlugin(file);
        } catch (InvalidDescriptionException ex) {
            return new BukkitLoadResult(Result.INVALID_DESCRIPTION);
        } catch (UnknownDependencyException ex) {
            return new BukkitLoadResult(Result.UNKNOWN_DEPENDENCY.arg(ex.getMessage()));
        } catch (InvalidPluginException ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                IllegalArgumentException e = (IllegalArgumentException) ex.getCause();
                if (e.getMessage().equalsIgnoreCase("Plugin already initialized!")) {
                    return new BukkitLoadResult(Result.ALREADY_ENABLED);
                }
            }
            ex.printStackTrace();
            return new BukkitLoadResult(Result.ERROR);
        }

        if (plugin == null) return new BukkitLoadResult(Result.INVALID_PLUGIN);
        plugin.onLoad();
        return new BukkitLoadResult(plugin);
    }

    /**
     * Disables the specified plugin by name and cleans all commands and recipes of the plugin.
     * @param pluginName The plugin to disable.
     * @return The result of the disable call.
     */
    public static Result disablePlugin(String pluginName) {
        return disablePlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    /**
     * Disables the specified plugin and cleans all commands and recipes of the plugin.
     * @param plugin The plugin to disable.
     * @return The result of the disable call.
     */
    public static Result disablePlugin(Plugin plugin) {
        if (plugin == null) return Result.NOT_ENABLED;
        if (!plugin.isEnabled()) return Result.ALREADY_DISABLED;
        try {
            Bukkit.getPluginManager().disablePlugin(plugin);
            RCraftingManager.removeRecipesFor(plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.ERROR;
        }
        unregisterCommands(plugin);
        return Result.SUCCESS;
    }

    /**
     * Unloads the specified plugin by name and cleans all traces within bukkit.
     * @param pluginName The plugin to unload.
     * @return The result of the unload.
     */
    @Override
    public CloseableResult unloadPlugin(String pluginName) {
        return unloadPlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    /**
     * Unloads the specified plugin and cleans all traces within bukkit.
     * @param plugin The plugin to unload.
     * @return The result of the unload.
     */
    @Override
    public CloseableResult unloadPlugin(Plugin plugin) {
        if (plugin == null) return new CloseableResult(Result.NOT_EXISTS);
        Closeable closeable;
        try {
            RSimplePluginManager.getPlugins(Bukkit.getPluginManager()).remove(plugin);
            RSimplePluginManager.removeLookupName(Bukkit.getPluginManager(), plugin.getName());
            closeable = RPluginClassLoader.clearClassLoader(RJavaPlugin.getClassLoader(plugin));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new CloseableResult(Result.ERROR);
        }
        return new CloseableResult(closeable);
    }

    /**
     * Enables the specified plugin by name.
     * @param pluginName The plugin to enable.
     * @return The result of the enabling.
     */
    public Result enablePlugin(String pluginName) {
        return enablePlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    /**
     * Enables the specified plugin.
     * @param plugin The plugin to enable.
     * @return The result of the enabling.
     */
    @Override
    public Result enablePlugin(Plugin plugin) {
        if (plugin == null) return Result.NOT_EXISTS;
        if (Bukkit.getPluginManager().isPluginEnabled(plugin.getName())) return Result.ALREADY_ENABLED;
        Bukkit.getPluginManager().enablePlugin(plugin);
        if (Bukkit.getPluginManager().isPluginEnabled(plugin.getName())) {
            return Result.SUCCESS;
        }
        return Result.ERROR;
    }

    /**
     * Reloads the specified plugin by name.
     * @param pluginName The plugin to reload.
     * @return The result of the reload.
     */
    @Override
    public Result reloadPlugin(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return Result.NOT_EXISTS;
        return reloadPlugin(plugin);
    }

    /**
     * Reloads the specified plugin.
     * @param plugin The plugin to reload.
     * @return The result of the reload.
     */
    @Override
    public Result reloadPlugin(Plugin plugin) {
        Result disableResult = disablePlugin(plugin);
        if (disableResult != Result.SUCCESS && disableResult != Result.ALREADY_DISABLED) {
            return disableResult;
        }

        CloseableResult result = unloadPlugin(plugin);
        if (result.getResult() != Result.SUCCESS) return result.getResult();
        result.tryClose();

        File pluginFile = getPluginFile(plugin.getName());
        if (pluginFile == null) return Result.FILE_DELETED;

        BukkitLoadResult loadResult = loadPlugin(pluginFile);
        if (!loadResult.isSuccess()) return loadResult.getResult();
        return enablePlugin(loadResult.get());
    }

    /**
     * Retrieves all known commands registered to bukkit.
     * @return A map with all known commands.
     */
    public static Map<String, Command> getKnownCommands() {
        try {
            return RCommandMap.getKnownCommands(RCraftServer.getCommandMap());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Unregisters all commands from the specified plugin.
     * @param plugin The plugin.
     */
    public static void unregisterCommands(Plugin plugin) {
        Map<String, Command> knownCommands = getKnownCommands();
        if (knownCommands == null) return;

        knownCommands.values().removeIf(c -> {
            if (c instanceof PluginCommand) {
                PluginCommand pc = (PluginCommand) c;
                if (pc.getPlugin() == plugin) {
                    pc.unregister(RCraftServer.getCommandMap());
                    return true;
                }
                return false;
            }
            return false;
        });
    }

    /**
     * Retrieves a command from the command map.
     * @param command The command string.
     * @return The command.
     */
    public static Command getCommand(String command) {
        Map<String, Command> knownCommands = getKnownCommands();
        if (knownCommands == null) return null;
        return knownCommands.get(command);
    }

    /**
     * Retrieves all file associations, i.e. all plugin loaders.
     * @return A map with all pluginloaders.
     */
    public static Map<Pattern, PluginLoader> getFileAssociations() {
        try {
            return RSimplePluginManager.getFileAssociations(Bukkit.getPluginManager());
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the PluginLoader for the input file.
     * @param file The file.
     * @return The appropiate PluginLoader.
     */
    public static PluginLoader getPluginLoader(File file) {
        Map<Pattern, PluginLoader> fileAssociations = getFileAssociations();
        if (fileAssociations == null) return null;

        for (Pattern filter : fileAssociations.keySet()) {
            Matcher match = filter.matcher(file.getName());
            if (match.find()) {
                return fileAssociations.get(filter);
            }
        }
        return null;
    }

    /**
     * Retrieves a loaded plugin associated to a jar file.
     * @param file The jar file.
     * @return The already loaded plugin, or null if none.
     */
    public static Plugin getLoadedPlugin(File file) {
        PluginDescriptionFile descriptionFile;
        try {
            descriptionFile = getPluginDescription(file);
        } catch (InvalidDescriptionException ex) {
            return null;
        }
        if (descriptionFile == null) return null;
        return Bukkit.getPluginManager().getPlugin(descriptionFile.getName());
    }

    /**
     * Retrieves the PluginDescriptionFile of a jar file.
     * @param file The jar file.
     * @return The PluginDescriptionFile.
     * @throws InvalidDescriptionException Iff the PluginDescriptionFile is invalid.
     */
    public static PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        PluginLoader loader = getPluginLoader(file);
        if (loader == null) return null;
        return loader.getPluginDescription(file);
    }

    /**
     * Attempts to retrieve the plugin file by plugin name.
     * @param pluginName The plugin name.
     * @return The file, or null if invalid or not found.
     */
    public File getPluginFile(String pluginName) {
        for (File file : getPluginJars()) {
            PluginDescriptionFile descriptionFile;
            try {
                descriptionFile = getPluginDescription(file);
            } catch (InvalidDescriptionException ex) {
                return null;
            }
            if (descriptionFile == null) return null;
            if (descriptionFile.getName().equals(pluginName)) return file;
        }
        return null;
    }

    @Override
    public File getPluginFile(Plugin plugin) {
        try {
            return RJavaPlugin.getFile(plugin);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Error retrieving current plugin file", ex);
        }
    }

    @Override
    public Set<String> getCommands() {
        Map<String, Command> knownCommands = getKnownCommands();
        if (knownCommands == null) return Collections.emptySet();
        return knownCommands.keySet();
    }

    @Override
    public File getPluginsFolder() {
        return plugin.getDataFolder().getParentFile();
    }

    @Override
    public List<Plugin> getPlugins() {
        return Arrays.asList(Bukkit.getPluginManager().getPlugins());
    }

    @Override
    public String getPluginName(Plugin plugin) {
        return plugin.getName();
    }
}
