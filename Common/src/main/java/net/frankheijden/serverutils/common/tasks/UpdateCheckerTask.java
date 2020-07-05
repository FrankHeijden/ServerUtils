package net.frankheijden.serverutils.common.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.managers.AbstractVersionManager;
import net.frankheijden.serverutils.common.config.Config;
import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.common.config.YamlConfig;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.utils.FileUtils;
import net.frankheijden.serverutils.common.utils.VersionUtils;

public class UpdateCheckerTask implements Runnable {

    private static final ServerUtilsPlugin plugin = ServerUtilsApp.getPlugin();
    private static final YamlConfig config = Config.getInstance().getConfig();

    private final AbstractVersionManager versionManager;
    private final ServerCommandSender sender;
    private final boolean startup;

    private static final String GITHUB_LINK = "https://api.github.com/repos/FrankHeijden/ServerUtils/releases/latest";

    private static final String UPDATE_CHECK_START = "Checking for updates...";
    private static final String GENERAL_ERROR = "Error fetching new version of ServerUtils";
    private static final String TRY_LATER = GENERAL_ERROR + ", please try again later!";
    private static final String CONNECTION_ERROR = GENERAL_ERROR + ": (%s) %s (maybe check your connection?)";
    private static final String UNAVAILABLE = GENERAL_ERROR + ": (%s) %s (no update available)";
    private static final String UPDATE_AVAILABLE = "ServerUtils %s is available!";
    private static final String DOWNLOAD_START = "Started downloading from \"%s\"...";
    private static final String DOWNLOAD_ERROR = "Error downloading a new version of ServerUtils";
    private static final String UPGRADE_SUCCESS = "Successfully upgraded ServerUtils to v%s!";
    private static final String DOWNLOADED_RESTART = "Downloaded ServerUtils version v%s. Restarting plugin now...";
    private static final String UP_TO_DATE = "We are up-to-date!";

    private UpdateCheckerTask(ServerCommandSender sender, boolean startup) {
        this.versionManager = plugin.getVersionManager();
        this.sender = sender;
        this.startup = startup;
    }

    public static void start(ServerCommandSender sender) {
        start(sender, false);
    }

    public static void start(ServerCommandSender sender, boolean startup) {
        UpdateCheckerTask task = new UpdateCheckerTask(sender, startup);
        plugin.getTaskManager().runTaskAsynchronously(task);
    }

    public boolean isStartupCheck() {
        return this.startup;
    }

    @Override
    public void run() {
        if (isStartupCheck()) {
            plugin.getLogger().info(UPDATE_CHECK_START);
        }

        JsonElement jsonElement;
        try {
            jsonElement = FileUtils.readJsonFromUrl(GITHUB_LINK);
        } catch (ConnectException | UnknownHostException | SocketTimeoutException ex) {
            plugin.getLogger().severe(String.format(CONNECTION_ERROR, ex.getClass().getSimpleName(), ex.getMessage()));
            return;
        } catch (FileNotFoundException ex) {
            plugin.getLogger().severe(String.format(UNAVAILABLE, ex.getClass().getSimpleName(), ex.getMessage()));
            return;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, ex, () -> GENERAL_ERROR);
            return;
        }

        if (jsonElement == null) {
            plugin.getLogger().warning(TRY_LATER);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("message")) {
            plugin.getLogger().warning(jsonObject.get("message").getAsString());
            return;
        }

        String githubVersion = getVersion(jsonObject);
        String body = jsonObject.getAsJsonPrimitive("body").getAsString();
        String downloadUrl = getDownloadUrl(jsonObject);

        String downloaded = versionManager.getDownloadedVersion();
        String current = versionManager.getCurrentVersion();
        if (VersionUtils.isNewVersion(downloaded, githubVersion)) {
            if (isStartupCheck()) {
                plugin.getLogger().info(String.format(UPDATE_AVAILABLE, githubVersion));
                plugin.getLogger().info("Release info: " + body);
            }
            if (canDownloadPlugin()) {
                if (isStartupCheck()) {
                    plugin.getLogger().info(String.format(DOWNLOAD_START, downloadUrl));
                } else {
                    Messenger.sendMessage(sender, "serverutils.update.downloading",
                            "%old%", current,
                            "%new%", githubVersion,
                            "%info%", body);
                }
                downloadPlugin(githubVersion, downloadUrl);
                tryReloadPlugin();
            } else if (!isStartupCheck()) {
                Messenger.sendMessage(sender, "serverutils.update.available",
                        "%old%", current,
                        "%new%", githubVersion,
                        "%info%", body);
            }
        } else if (versionManager.hasDownloaded()) {
            Messenger.sendMessage(sender,  "serverutils.update.success",
                    "%new%", versionManager.getDownloadedVersion());
        } else if (isStartupCheck()) {
            plugin.getLogger().info(UP_TO_DATE);
        }
    }

    private String getVersion(JsonObject jsonObject) {
        return jsonObject.getAsJsonPrimitive("tag_name").getAsString().replace("v", "");
    }

    private String getDownloadUrl(JsonObject jsonObject) {
        JsonArray assets = jsonObject.getAsJsonArray("assets");
        if (assets != null && assets.size() > 0) {
            return assets.get(0).getAsJsonObject().getAsJsonPrimitive("browser_download_url").getAsString();
        }
        return null;
    }

    private boolean canDownloadPlugin() {
        if (isStartupCheck()) return config.getBoolean("settings.download-at-startup-and-update");
        return config.getBoolean("settings.download-updates");
    }

    private void downloadPlugin(String githubVersion, String downloadLink) {
        if (versionManager.isDownloaded(githubVersion)) {
            broadcastDownloadStatus(githubVersion, false);
            return;
        }

        if (downloadLink == null) {
            broadcastDownloadStatus(githubVersion, true);
            return;
        }

        try {
            FileUtils.download(downloadLink, plugin.getPluginManager().getPluginFile(ServerUtilsApp.getPlatformPlugin()));
        } catch (IOException ex) {
            broadcastDownloadStatus(githubVersion, true);
            throw new RuntimeException(DOWNLOAD_ERROR, ex);
        }

        versionManager.setDownloaded(githubVersion);
    }

    private void tryReloadPlugin() {
        String downloadedVersion = versionManager.getDownloadedVersion();

        if (isStartupCheck()) {
            plugin.getLogger().info(String.format(DOWNLOADED_RESTART, downloadedVersion));
            CloseableResult result = plugin.getPluginManager().reloadPlugin(ServerUtilsApp.getPlatformPlugin());
            plugin.getLogger().info(String.format(UPGRADE_SUCCESS, downloadedVersion));
            result.tryClose();
        } else {
            broadcastDownloadStatus(downloadedVersion, false);
        }
    }

    private void broadcastDownloadStatus(String githubVersion, boolean isError) {
        final String path = "serverutils.update." + (isError ? "failed" : "success");
        String message = Messenger.getMessage(path,"%new%", githubVersion);
        plugin.getChatProvider().broadcast("serverutils.notification.update", message);
    }
}
