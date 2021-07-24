package net.frankheijden.serverutils.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;

public class ServerUtilsApp<U extends ServerUtilsPlugin<P, T, C, S>, P, T, C extends ServerCommandSender<S>, S> {

    public static final int BSTATS_METRICS_ID = 7790;
    public static final String VERSION = "{version}";

    private final Object platformPlugin;
    private final U plugin;

    @SuppressWarnings("rawtypes")
    private static ServerUtilsApp instance;

    private ServerUtilsApp(Object platformPlugin, U plugin) {
        this.platformPlugin = platformPlugin;
        this.plugin = plugin;
    }

    public static <U extends ServerUtilsPlugin<P, T, C, S>, P, T, C extends ServerCommandSender<S>, S> void init(
            Object platformPlugin,
            U plugin
    ) {
        instance = new ServerUtilsApp<>(platformPlugin, plugin);
    }

    /**
     * Tries checking for updates if enabled by the config.
     */
    public static void tryCheckForUpdates() {
        UpdateCheckerTask.tryStart(getPlugin(), getPlugin().getChatProvider().getConsoleSender(), "boot");
    }

    /**
     * Unloads the ServerUtilsUpdater and deletes the file.
     */
    public static <U extends ServerUtilsPlugin<P, T, C, S>, P, T, C extends ServerCommandSender<S>, S>
        void unloadServerUtilsUpdater() {
        U plugin = getPlugin();
        plugin.getTaskManager().runTaskLater(() -> {
            String updaterName = plugin.getPlatform() == ServerUtilsPlugin.Platform.VELOCITY
                    ? "serverutilsupdater"
                    : "ServerUtilsUpdater";
            P updaterPlugin = plugin.getPluginManager().getPlugin(updaterName);
            if (updaterPlugin == null) return;

            @SuppressWarnings("VariableDeclarationUsageDistance")
            File file = plugin.getPluginManager().getPluginFile(updaterPlugin);
            Result result = plugin.getPluginManager().disablePlugin(updaterPlugin);
            if (result != Result.SUCCESS) {
                result.sendTo(plugin.getChatProvider().getConsoleSender(), "disabl", updaterName);
                return;
            }

            CloseableResult closeableResult = plugin.getPluginManager().unloadPlugin(updaterName);
            if (closeableResult.getResult() != Result.SUCCESS) {
                closeableResult.getResult().sendTo(plugin.getChatProvider().getConsoleSender(), "unload", updaterName);
            }

            closeableResult.tryClose();

            if (Files.exists(file.toPath())) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, 10);
    }

    public static Object getPlatformPlugin() {
        return instance.platformPlugin;
    }

    @SuppressWarnings("unchecked")
    public static <U extends ServerUtilsPlugin<P, T, C, S>, P, T, C extends ServerCommandSender<S>, S>
        U getPlugin() {
        return (U) instance.plugin;
    }
}
