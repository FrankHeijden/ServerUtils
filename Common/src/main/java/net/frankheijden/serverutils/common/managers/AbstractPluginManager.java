package net.frankheijden.serverutils.common.managers;

import java.io.File;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.entities.AbstractResult;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.WatchResult;
import net.frankheijden.serverutils.common.providers.PluginProvider;
import net.frankheijden.serverutils.common.tasks.PluginWatcherTask;

public interface AbstractPluginManager<P> extends PluginProvider<P> {

    LoadResult<P> loadPlugin(String pluginFile);

    LoadResult<P> loadPlugin(File file);

    Result enablePlugin(P plugin);

    Result disablePlugin(P plugin);

    Result reloadPlugin(String pluginName);

    Result reloadPlugin(P plugin);

    CloseableResult unloadPlugin(String pluginName);

    CloseableResult unloadPlugin(P plugin);

    /**
     * Starts watching the specified plugin for changes.
     * Reloads the plugin if a change is detected.
     * @param pluginName The plugin to watch.
     * @return The result of the action.
     */
    default AbstractResult watchPlugin(ServerCommandSender<?> sender, String pluginName) {
        if (getPlugin(pluginName) == null) return Result.NOT_EXISTS;
        ServerUtilsApp.getPlugin().getTaskManager()
                .runTaskAsynchronously(pluginName, new PluginWatcherTask(sender, pluginName));
        return WatchResult.START;
    }

    /**
     * Stops watching the plugin for changes.
     * @param pluginName The plugin to stop watching.
     * @return The result of the action.
     */
    default AbstractResult unwatchPlugin(String pluginName) {
        if (ServerUtilsApp.getPlugin().getTaskManager().cancelTask(pluginName)) return WatchResult.STOPPED;
        return WatchResult.NOT_WATCHING;
    }
}
