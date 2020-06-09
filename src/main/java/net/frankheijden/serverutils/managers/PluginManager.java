package net.frankheijden.serverutils.managers;

import net.frankheijden.serverutils.ServerUtils;
import net.frankheijden.serverutils.reflection.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.PluginClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.set;

public class PluginManager {

    public static LoadResult loadPlugin(String jarFile) {
        return loadPlugin(new File(ServerUtils.getInstance().getDataFolder().getParent(), jarFile));
    }

    public static LoadResult loadPlugin(File file) {
        if (!file.exists()) return new LoadResult(Result.NOT_EXISTS);
        try {
            return new LoadResult(Bukkit.getPluginManager().loadPlugin(file), Result.SUCCESS);
        } catch (InvalidDescriptionException ex) {
            return new LoadResult(Result.INVALID_DESCRIPTION);
        } catch (InvalidPluginException ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                IllegalArgumentException e = (IllegalArgumentException) ex.getCause();
                if (e.getMessage().equalsIgnoreCase("Plugin already initialized!")) {
                    return new LoadResult(Result.ALREADY_ENABLED);
                }
            }
            ex.printStackTrace();
        }

        return new LoadResult(Result.ERROR);
    }

    public static Result disablePlugin(String pluginName) {
        return disablePlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public static Result disablePlugin(Plugin plugin) {
        if (plugin == null) return Result.NOT_ENABLED;
        try {
            Bukkit.getPluginManager().disablePlugin(plugin);
            RSimplePluginManager.getPlugins(Bukkit.getPluginManager()).remove(plugin);
            RSimplePluginManager.removeLookupName(Bukkit.getPluginManager(), plugin.getName());
            clearClassLoader(RJavaPlugin.getClassLoader(plugin));
            RCraftingManager.removeRecipesFor(plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.ERROR;
        }
        unregisterCommands(plugin);
        return Result.SUCCESS;
    }

    public static void clearClassLoader(ClassLoader loader) throws IllegalAccessException {
        if (loader == null) return;
        if (loader instanceof PluginClassLoader) {
            clearClassLoader((PluginClassLoader) loader);
        }
    }

    public static void clearClassLoader(PluginClassLoader loader) throws IllegalAccessException {
        if (loader == null) return;
        set(RPluginClassLoader.getFields(), loader, "plugin", null);
        set(RPluginClassLoader.getFields(), loader, "pluginInit", null);
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
        if (disableResult != Result.SUCCESS) return disableResult;

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
}
