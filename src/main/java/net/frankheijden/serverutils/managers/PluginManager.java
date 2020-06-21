package net.frankheijden.serverutils.managers;

import net.frankheijden.serverutils.ServerUtils;
import net.frankheijden.serverutils.reflection.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.PluginClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.set;

public class PluginManager {

    public static LoadResult loadPlugin(String jarFile) {
        return loadPlugin(new File(ServerUtils.getInstance().getDataFolder().getParent(), jarFile));
    }

    public static LoadResult loadPlugin(File file) {
        if (!file.exists()) return new LoadResult(Result.NOT_EXISTS);

        Plugin loadedPlugin = getLoadedPlugin(file);
        if (loadedPlugin != null) return new LoadResult(Result.ALREADY_LOADED);

        Plugin plugin;
        try {
            plugin = Bukkit.getPluginManager().loadPlugin(file);
        } catch (InvalidDescriptionException ex) {
            return new LoadResult(Result.INVALID_DESCRIPTION);
        } catch (UnknownDependencyException ex) {
            return new LoadResult(Result.UNKNOWN_DEPENDENCY.arg(ex.getMessage()));
        } catch (InvalidPluginException ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                IllegalArgumentException e = (IllegalArgumentException) ex.getCause();
                if (e.getMessage().equalsIgnoreCase("Plugin already initialized!")) {
                    return new LoadResult(Result.ALREADY_ENABLED);
                }
            }
            ex.printStackTrace();
            return new LoadResult(Result.ERROR);
        }

        if (plugin == null) return new LoadResult(Result.INVALID_PLUGIN);
        plugin.onLoad();
        return new LoadResult(plugin);
    }

    public static Result disablePlugin(String pluginName) {
        return disablePlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

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

    public static Result unloadPlugin(String pluginName) {
        return unloadPlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public static Result unloadPlugin(Plugin plugin) {
        if (plugin == null) return Result.NOT_EXISTS;
        try {
            RSimplePluginManager.getPlugins(Bukkit.getPluginManager()).remove(plugin);
            RSimplePluginManager.removeLookupName(Bukkit.getPluginManager(), plugin.getName());
            clearClassLoader(RJavaPlugin.getClassLoader(plugin));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.ERROR;
        }
        return Result.SUCCESS;
    }

    public static void clearClassLoader(ClassLoader loader) throws IllegalAccessException, IOException {
        if (loader == null) return;
        if (loader instanceof PluginClassLoader) {
            clearClassLoader((PluginClassLoader) loader);
        }
    }

    public static void clearClassLoader(PluginClassLoader loader) throws IllegalAccessException, IOException {
        if (loader == null) return;
        set(RPluginClassLoader.getFields(), loader, "plugin", null);
        set(RPluginClassLoader.getFields(), loader, "pluginInit", null);
        loader.close();
    }

    public static Result enablePlugin(String pluginName) {
        return enablePlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public static Result enablePlugin(Plugin plugin) {
        if (plugin == null) return Result.NOT_EXISTS;
        if (Bukkit.getPluginManager().isPluginEnabled(plugin.getName())) return Result.ALREADY_ENABLED;
        Bukkit.getPluginManager().enablePlugin(plugin);
        if (Bukkit.getPluginManager().isPluginEnabled(plugin.getName())) {
            return Result.SUCCESS;
        }
        return Result.ERROR;
    }

    public static Result reloadPlugin(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return Result.NOT_EXISTS;
        return reloadPlugin(plugin);
    }

    public static Result reloadPlugin(Plugin plugin) {
        Result disableResult = disablePlugin(plugin);
        if (disableResult != Result.SUCCESS && disableResult != Result.ALREADY_DISABLED) return disableResult;

        Result unloadResult = unloadPlugin(plugin);
        if (unloadResult != Result.SUCCESS) return unloadResult;

        File pluginFile;
        try {
            pluginFile = RPlugin.getPluginFile(plugin);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
            return Result.ERROR;
        }

        LoadResult loadResult = loadPlugin(pluginFile);
        if (!loadResult.isSuccess()) {
            Result r = loadResult.getResult();
            return (r == Result.NOT_EXISTS) ? Result.FILE_CHANGED : r;
        }
        return enablePlugin(loadResult.getPlugin());
    }

    public static Map<String, Command> getKnownCommands() {
        try {
            return RCommandMap.getKnownCommands(RCraftServer.getCommandMap());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

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

    public static Command getCommand(String command) {
        Map<String, Command> knownCommands = getKnownCommands();
        if (knownCommands == null) return null;
        return knownCommands.get(command);
    }

    public static Map<Pattern, PluginLoader> getFileAssociations() {
        try {
            return RSimplePluginManager.getFileAssociations(Bukkit.getPluginManager());
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

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

    public static PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        PluginLoader loader = getPluginLoader(file);
        if (loader == null) return null;
        return loader.getPluginDescription(file);
    }
}
