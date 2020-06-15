package net.frankheijden.serverutils.tasks;

import com.google.gson.*;
import net.frankheijden.serverutils.ServerUtils;
import net.frankheijden.serverutils.config.Config;
import net.frankheijden.serverutils.config.Messenger;
import net.frankheijden.serverutils.managers.*;
import net.frankheijden.serverutils.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class UpdateCheckerTask implements Runnable {

    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static final VersionManager versionManager = VersionManager.getInstance();
    private final CommandSender sender;
    private final String currentVersion;
    private final boolean startup;

    private static final String GITHUB_LINK = "https://api.github.com/repos/FrankHeijden/ServerUtils/releases/latest";

    private UpdateCheckerTask(CommandSender sender, boolean startup) {
        this.sender = sender;
        this.currentVersion = plugin.getDescription().getVersion();
        this.startup = startup;
    }

    public static void start(CommandSender sender) {
        start(sender, false);
    }

    public static void start(CommandSender sender, boolean startup) {
        UpdateCheckerTask task = new UpdateCheckerTask(sender, startup);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public boolean isStartupCheck() {
        return this.startup;
    }

    @Override
    public void run() {
        if (isStartupCheck()) {
            plugin.getLogger().info("Checking for updates...");
        }

        JsonObject jsonObject;
        try {
            jsonObject = readJsonFromURL(GITHUB_LINK).getAsJsonObject();
        } catch (ConnectException | UnknownHostException | SocketTimeoutException ex) {
            plugin.getLogger().severe(String.format("Error fetching new version of ServerUtils: (%s) %s (maybe check your connection?)",
                    ex.getClass().getSimpleName(), ex.getMessage()));
            return;
        } catch (FileNotFoundException ex) {
            plugin.getLogger().severe(String.format("Error fetching new version of ServerUtils: (%s) %s (no update available)",
                    ex.getClass().getSimpleName(), ex.getMessage()));
            return;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, ex, () -> "Error fetching new version of ServerUtils");
            return;
        }
        String githubVersion = jsonObject.getAsJsonPrimitive("tag_name").getAsString();
        githubVersion = githubVersion.replace("v", "");
        String body = jsonObject.getAsJsonPrimitive("body").getAsString();

        JsonArray assets = jsonObject.getAsJsonArray("assets");
        String downloadLink = null;
        if (assets != null && assets.size() > 0) {
            downloadLink = assets.get(0)
                    .getAsJsonObject()
                    .getAsJsonPrimitive("browser_download_url")
                    .getAsString();
        }
        if (VersionUtils.isNewVersion(currentVersion, githubVersion)) {
            if (isStartupCheck()) {
                plugin.getLogger().info(String.format("ServerUtils %s is available!", githubVersion));
                plugin.getLogger().info("Release info: " + body);
            }
            if (canDownloadPlugin()) {
                if (isStartupCheck()) {
                    plugin.getLogger().info("Started downloading from \"" + downloadLink + "\"...");
                } else {
                    Messenger.sendMessage(sender, "serverutils.update.downloading",
                            "%old%", currentVersion,
                            "%new%", githubVersion,
                            "%info%", body);
                }
                downloadPlugin(githubVersion, downloadLink);
            } else if (!isStartupCheck()) {
                Messenger.sendMessage(sender, "serverutils.update.available",
                        "%old%", currentVersion,
                        "%new%", githubVersion,
                        "%info%", body);
            }
        } else if (versionManager.hasDownloaded()) {
            Messenger.sendMessage(sender, "serverutils.update.success",
                    "%new%", versionManager.getDownloadedVersion());
        } else if (isStartupCheck()) {
            plugin.getLogger().info("We are up-to-date!");
        }
    }

    private boolean canDownloadPlugin() {
        if (isStartupCheck()) return Config.getInstance().getBoolean("settings.download-at-startup-and-update");
        return Config.getInstance().getBoolean("settings.download-updates");
    }

    private void downloadPlugin(String githubVersion, String downloadLink) {
        if (versionManager.isDownloadedVersion(githubVersion)) {
            broadcastDownloadStatus(githubVersion, false);
            return;
        }

        if (downloadLink == null) {
            broadcastDownloadStatus(githubVersion, true);
            return;
        }

        try {
            download(downloadLink, getPluginFile());
        } catch (IOException ex) {
            broadcastDownloadStatus(githubVersion, true);
            throw new RuntimeException("Error downloading a new version of ServerUtils", ex);
        }

        if (isStartupCheck()) {
            plugin.getLogger().info(String.format("Downloaded ServerUtils version v%s. Restarting plugin now...", githubVersion));
            Bukkit.getPluginManager().disablePlugin(plugin);
            try {
                Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(getPluginFile()));
            } catch (InvalidPluginException | InvalidDescriptionException ex) {
                ex.printStackTrace();
                return;
            }
            plugin.getLogger().info(String.format("Successfully upgraded ServerUtils to v%s!", githubVersion));
        } else {
            versionManager.setDownloadedVersion(githubVersion);
            broadcastDownloadStatus(githubVersion, false);
        }
    }

    private void broadcastDownloadStatus(String githubVersion, boolean isError) {
        final String path = "serverutils.update." + (isError ? "failed" : "success");
        Bukkit.getOnlinePlayers().forEach((p) -> {
            if (p.hasPermission("serverutils.notification.update")) {
                Messenger.sendMessage(sender, path, "%new%", githubVersion);
            }
        });
    }

    private File getPluginFile() {
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return (File) method.invoke(plugin);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Error retrieving current plugin file", ex);
        }
    }

    private void download(String urlString, File target) throws IOException {
        try (InputStream is = stream(urlString);
             ReadableByteChannel rbc = Channels.newChannel(is);
             FileOutputStream fos = new FileOutputStream(target)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private JsonElement readJsonFromURL(String url) throws IOException {
        try (InputStream is = stream(url)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(reader);
            return new JsonParser().parse(jsonText);
        }
    }

    private InputStream stream(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:77.0) Gecko/20100101 Firefox/77.0");
        conn.setConnectTimeout(10000);
        return conn.getInputStream();
    }
}
