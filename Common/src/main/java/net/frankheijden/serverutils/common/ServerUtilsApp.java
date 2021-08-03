package net.frankheijden.serverutils.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import net.frankheijden.serverutils.common.entities.ServerUtilsPluginDescription;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResult;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;
import net.kyori.adventure.text.Component;

public class ServerUtilsApp<U extends ServerUtilsPlugin<P, T, C, S, D>, P, T, C extends ServerUtilsAudience<S>, S, D extends ServerUtilsPluginDescription> {

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

    public static <
            U extends ServerUtilsPlugin<P, T, C, S, D>,
            P,
            T,
            C extends ServerUtilsAudience<S>,
            S,
            D extends ServerUtilsPluginDescription
        > void init(
            Object platformPlugin,
            U plugin
    ) {
        instance = new ServerUtilsApp<>(platformPlugin, plugin);
    }

    /**
     * Tries checking for updates if enabled by the config.
     */
    public static void tryCheckForUpdates() {
        UpdateCheckerTask.tryStart(getPlugin(), getPlugin().getChatProvider().getConsoleServerAudience(), "boot");
    }

    /**
     * Unloads the ServerUtilsUpdater and deletes the file.
     */
    public static <P> void unloadServerUtilsUpdater() {
        ServerUtilsPlugin<P, ?, ?, ?, ?> plugin = getPlugin();
        plugin.getTaskManager().runTaskLater(() -> {
            String updaterName = plugin.getPlatform() == ServerUtilsPlugin.Platform.VELOCITY
                    ? "serverutilsupdater"
                    : "ServerUtilsUpdater";
            Optional<P> updaterPluginOptional = plugin.getPluginManager().getPlugin(updaterName);
            if (!updaterPluginOptional.isPresent()) return;
            P updaterPlugin = updaterPluginOptional.get();

            @SuppressWarnings("VariableDeclarationUsageDistance")
            File file = plugin.getPluginManager().getPluginFile(updaterPlugin);
            PluginResult<P> disableResult = plugin.getPluginManager().disablePlugin(updaterPlugin);
            if (!disableResult.isSuccess()) {
                Component component = disableResult.toComponent(null);
                plugin.getChatProvider().getConsoleServerAudience().sendMessage(component);
                return;
            }

            CloseablePluginResult<P> unloadResult = plugin.getPluginManager().unloadPlugin(disableResult.getPlugin());
            if (!unloadResult.isSuccess()) {
                Component component = unloadResult.toComponent(null);
                plugin.getChatProvider().getConsoleServerAudience().sendMessage(component);
                return;
            }

            unloadResult.tryClose();

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
    public static <
            U extends ServerUtilsPlugin<P, T, C, S, D>,
            P,
            T,
            C extends ServerUtilsAudience<S>,
            S,
            D extends ServerUtilsPluginDescription
        > U getPlugin() {
        return (U) instance.plugin;
    }
}
