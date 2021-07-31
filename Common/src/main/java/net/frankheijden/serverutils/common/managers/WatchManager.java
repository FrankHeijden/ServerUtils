package net.frankheijden.serverutils.common.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.entities.results.WatchResult;
import net.frankheijden.serverutils.common.tasks.PluginWatcherTask;

public class WatchManager<P, T> {

    private final ServerUtilsPlugin<P, T, ?, ?, ?> plugin;
    private final Map<String, WatchTask> watchTasks;

    public WatchManager(ServerUtilsPlugin<P, T, ?, ?, ?> plugin) {
        this.plugin = plugin;
        this.watchTasks = new HashMap<>();
    }

    /**
     * Starts watching the specified plugin and reloads it when a change is detected.
     */
    public WatchResult watchPlugins(ServerCommandSender<?> sender, List<P> plugins) {
        List<String> pluginIds = new ArrayList<>(plugins.size());
        for (P watchPlugin : plugins) {
            String pluginId = plugin.getPluginManager().getPluginId(watchPlugin);
            if (watchTasks.containsKey(pluginId)) {
                return WatchResult.ALREADY_WATCHING.arg(pluginId);
            }

            pluginIds.add(plugin.getPluginManager().getPluginId(watchPlugin));
        }

        UUID key = UUID.randomUUID();
        plugin.getTaskManager().runTaskAsynchronously(
                key.toString(),
                new PluginWatcherTask<>(plugin, sender, plugins)
        );

        WatchTask watchTask = new WatchTask(key, pluginIds);
        for (String pluginId : pluginIds) {
            watchTasks.put(pluginId, watchTask);
        }

        return WatchResult.START.args(pluginIds);
    }

    /**
     * Stops watching plugins for changes.
     */
    public WatchResult unwatchPluginsAssociatedWith(String pluginId) {
        WatchTask task = watchTasks.get(pluginId);
        if (task != null && plugin.getTaskManager().cancelTask(task.key.toString())) {
            task.pluginIds.forEach(watchTasks::remove);
            return WatchResult.STOPPED.args(task.pluginIds);
        }
        return WatchResult.NOT_WATCHING.arg(pluginId);
    }

    private static final class WatchTask {

        private final UUID key;
        private final List<String> pluginIds;

        private WatchTask(UUID key, List<String> pluginIds) {
            this.key = key;
            this.pluginIds = pluginIds;
        }
    }
}
