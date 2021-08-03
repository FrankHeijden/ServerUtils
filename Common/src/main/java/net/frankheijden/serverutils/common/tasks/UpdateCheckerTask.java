package net.frankheijden.serverutils.common.tasks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.ConfigKey;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.entities.results.Result;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.entities.http.GitHubAsset;
import net.frankheijden.serverutils.common.entities.http.GitHubResponse;
import net.frankheijden.serverutils.common.managers.UpdateManager;
import net.frankheijden.serverutils.common.utils.GitHubUtils;
import net.frankheijden.serverutils.common.utils.VersionUtils;
import net.frankheijden.serverutilsupdater.common.Updater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

public class UpdateCheckerTask<U extends ServerUtilsPlugin<P, ?, ?, ?, ?>, P> implements Runnable {

    private final U plugin;
    private final ServerUtilsAudience<?> sender;
    private final boolean download;
    private final boolean install;

    private static final String GITHUB_LINK = "https://api.github.com/repos/FrankHeijden/ServerUtils/releases/latest";
    private static final String GITHUB_UPDATER_LINK = "https://api.github.com/repos/FrankHeijden/ServerUtilsUpdater"
            + "/releases/latest";

    private static final String UPDATE_CHECK_START = "Checking for updates...";
    private static final String RATE_LIMIT = "Received ratelimit from GitHub.";
    private static final String GENERAL_ERROR = "Error fetching new version of ServerUtils";
    private static final String TRY_LATER = GENERAL_ERROR + ", please try again later!";
    private static final String CONNECTION_ERROR = GENERAL_ERROR + ": ({0}) {1} (maybe check your connection?)";
    private static final String UNAVAILABLE = GENERAL_ERROR + ": ({0}) {1} (no update available)";
    private static final String UPDATE_AVAILABLE = "ServerUtils {0} is available!";
    private static final String RELEASE_INFO = "Release info: {0}";
    private static final String DOWNLOAD_START = "Started downloading from \"{0}\"...";
    private static final String DOWNLOAD_ERROR = "Error downloading a new version of ServerUtils";
    private static final String DOWNLOADED = "Downloaded ServerUtils version v{0}.";
    private static final String DOWNLOADED_RESTART = DOWNLOADED + " Restarting plugin now...";
    private static final String UPDATER_LOAD_ERROR = "Failed to load ServerUtilsUpdater: {0}";
    private static final String UPDATER_ENABLE_ERROR = "Failed to enable ServerUtilsUpdater: {0}";
    private static final String UP_TO_DATE = "We are up-to-date!";

    private UpdateCheckerTask(U plugin, ServerUtilsAudience<?> sender, boolean download, boolean install) {
        this.plugin = plugin;
        this.sender = sender;
        this.download = download;
        this.install = install;
    }

    /**
     * Checks for updates if enabled per config for the specific action.
     * Action must be 'login' or 'boot'.
     */
    public static <U extends ServerUtilsPlugin<P, ?, ?, ?, ?>, P> void tryStart(
            U plugin,
            ServerUtilsAudience<?> sender,
            String action
    ) {
        ServerUtilsConfig config = ServerUtilsApp.getPlugin().getConfigResource().getConfig();
        if (config.getBoolean("settings.check-updates-" + action)) {
            start(plugin, sender, action);
        }
    }

    /**
     * Checks for updates and downloads/installs if configured.
     * Action must be 'login' or 'boot'.
     */
    public static <U extends ServerUtilsPlugin<P, ?, ?, ?, ?>, P> void start(
            U plugin,
            ServerUtilsAudience<?> sender,
            String action
    ) {
        ServerUtilsConfig config = ServerUtilsApp.getPlugin().getConfigResource().getConfig();
        ServerUtilsApp.getPlugin().getTaskManager().runTaskAsynchronously(new UpdateCheckerTask<>(
                plugin,
                sender,
                config.getBoolean("settings.download-updates-" + action),
                config.getBoolean("settings.install-updates-" + action)
        ));
    }

    @Override
    public void run() {
        UpdateManager updateManager = plugin.getUpdateManager();
        if (!updateManager.canRunUpdateCheck()) return;
        updateManager.updateLastUpdateCheck();

        plugin.getLogger().info(UPDATE_CHECK_START);

        GitHubResponse pluginResponse = getResponse(GITHUB_LINK);
        if (pluginResponse == null) return;

        JsonObject pluginJson = getJson(pluginResponse);
        if (pluginJson == null) return;

        String githubVersion = getVersion(pluginJson);
        String body = pluginJson.getAsJsonPrimitive("body").getAsString();

        if (!VersionUtils.isNewVersion(updateManager.getDownloadedVersion(), githubVersion)) {
            if (updateManager.hasDownloaded()) {
                broadcastDownloadStatus(githubVersion, false);
            } else {
                plugin.getLogger().info(UP_TO_DATE);
            }
            return;
        }

        plugin.getLogger().log(Level.INFO, UPDATE_AVAILABLE, githubVersion);
        plugin.getLogger().log(Level.INFO, RELEASE_INFO, body);

        GitHubAsset pluginAsset = GitHubAsset.from(pluginJson);
        if (!download || pluginAsset == null) {
            if (sender.isPlayer()) {
                Component component = plugin.getMessagesResource().get(MessageKey.UPDATE_AVAILABLE).toComponent(
                        Template.of("old", ServerUtilsApp.VERSION),
                        Template.of("new", githubVersion),
                        Template.of("info", body)
                );
                sender.sendMessage(component);
            }
            return;
        }

        plugin.getLogger().log(Level.INFO, DOWNLOAD_START, pluginAsset.getDownloadUrl());
        if (sender.isPlayer()) {
            Component component = plugin.getMessagesResource().get(MessageKey.UPDATE_DOWNLOADING).toComponent(
                    Template.of("old", ServerUtilsApp.VERSION),
                    Template.of("new", githubVersion),
                    Template.of("info", body)
            );
            sender.sendMessage(component);
        }

        File pluginTarget = new File(plugin.getPluginManager().getPluginsFolder(), pluginAsset.getName());
        download(githubVersion, pluginAsset.getDownloadUrl(), pluginTarget);
        updateManager.setDownloadedVersion(githubVersion);
        if (!install) {
            deletePlugin();
            if (sender.isPlayer()) {
                broadcastDownloadStatus(githubVersion, false);
            } else {
                plugin.getLogger().log(Level.INFO, DOWNLOADED, githubVersion);
            }
            return;
        }

        GitHubResponse updaterResponse = getResponse(GITHUB_UPDATER_LINK);
        if (updaterResponse == null) return;

        JsonObject updaterJson = getJson(updaterResponse);
        if (updaterJson == null) return;

        GitHubAsset updaterAsset = GitHubAsset.from(updaterJson);
        if (updaterAsset == null) return;

        plugin.getLogger().log(Level.INFO, DOWNLOAD_START, updaterAsset.getDownloadUrl());
        File updaterTarget = new File(plugin.getPluginManager().getPluginsFolder(), updaterAsset.getName());
        download(githubVersion, updaterAsset.getDownloadUrl(), updaterTarget);
        plugin.getLogger().log(Level.INFO, DOWNLOADED_RESTART, githubVersion);

        deletePlugin();
        tryReloadPlugin(pluginTarget, updaterTarget);
    }

    private GitHubResponse getResponse(String urlString) {
        try {
            GitHubResponse response = GitHubUtils.stream(urlString);
            if (!response.getRateLimit().isRateLimited()) return response;
            plugin.getLogger().info(RATE_LIMIT);
        } catch (ConnectException | UnknownHostException | SocketTimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, CONNECTION_ERROR, new Object[] {
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            });
        } catch (FileNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, UNAVAILABLE, new Object[] {
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            });
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, ex, () -> GENERAL_ERROR);
        }
        return null;
    }

    private JsonObject getJson(GitHubResponse res) {
        JsonElement jsonElement;
        try {
            jsonElement = GitHubUtils.parseJson(res);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, ex, () -> GENERAL_ERROR);
            return null;
        }

        if (jsonElement == null) {
            plugin.getLogger().warning(TRY_LATER);
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("message")) {
            plugin.getLogger().warning(jsonObject.get("message").getAsString());
            return null;
        }
        return jsonObject;
    }

    private String getVersion(JsonObject jsonObject) {
        return jsonObject.getAsJsonPrimitive("tag_name").getAsString().replace("v", "");
    }

    private void download(String githubVersion, String downloadLink, File target) {
        if (downloadLink == null) {
            broadcastDownloadStatus(githubVersion, true);
            return;
        }

        try {
            GitHubResponse response = GitHubUtils.stream(downloadLink);
            if (response.getRateLimit().isRateLimited()) {
                plugin.getLogger().info(RATE_LIMIT);
                return;
            }

            GitHubUtils.download(response, target);
        } catch (IOException ex) {
            broadcastDownloadStatus(githubVersion, true);
            throw new RuntimeException(DOWNLOAD_ERROR, ex);
        }
    }

    private void deletePlugin() {
        plugin.getPluginManager().getPluginFile(plugin.getPlugin()).delete();
    }

    private void tryReloadPlugin(File pluginFile, File updaterFile) {
        plugin.getTaskManager().runTask(() -> {
            PluginResult<P> loadResult = plugin.getPluginManager().loadPlugin(updaterFile);
            if (!loadResult.isSuccess()) {
                plugin.getLogger().log(Level.INFO, UPDATER_LOAD_ERROR, loadResult.getResult().name());
                return;
            }

            PluginResult<P> enableResult = plugin.getPluginManager().enablePlugin(loadResult.getPlugin());
            if (!enableResult.isSuccess() && enableResult.getResult() != Result.ALREADY_ENABLED) {
                plugin.getLogger().log(Level.INFO, UPDATER_ENABLE_ERROR, enableResult.getResult().name());
                return;
            }

            Updater updater = (Updater) plugin.getPluginManager().getInstance(enableResult.getPlugin());
            updater.update(pluginFile);
            updaterFile.delete();
        });
    }

    private void broadcastDownloadStatus(String githubVersion, boolean isError) {
        ConfigKey key = isError ? MessageKey.UPDATE_DOWNLOAD_FAILED : MessageKey.UPDATE_DOWNLOAD_SUCCESS;
        Component component = plugin.getMessagesResource().get(key).toComponent(Template.of("new", githubVersion));
        plugin.getChatProvider().broadcast(component, "serverutils.notification.update");
    }
}
