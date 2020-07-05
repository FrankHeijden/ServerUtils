package net.frankheijden.serverutils.common;

import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;

public class ServerUtilsApp<T> {

    public static final int BSTATS_METRICS_ID = 7790;

    private final T platformPlugin;
    private final ServerUtilsPlugin plugin;

    @SuppressWarnings("rawtypes")
    private static ServerUtilsApp instance;

    private ServerUtilsApp(T platformPlugin, ServerUtilsPlugin plugin) {
        this.platformPlugin = platformPlugin;
        this.plugin = plugin;
        instance = this;
    }

    public static <T> void init(T obj, ServerUtilsPlugin plugin) {
        new ServerUtilsApp<>(obj, plugin);
    }

    /**
     * Tries checking for updates if enabled by the config.
     */
    public static void tryCheckForUpdates() {
        if (Config.getInstance().getConfig().getBoolean("settings.check-updates")) {
            UpdateCheckerTask.start(getPlugin().getChatProvider().getConsoleSender(), true);
        }
    }

    public static ServerUtilsPlugin getPlugin() {
        return instance.plugin;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPlatformPlugin() {
        return (T) instance.platformPlugin;
    }
}
