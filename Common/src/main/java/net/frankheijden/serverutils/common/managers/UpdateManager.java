package net.frankheijden.serverutils.common.managers;

import net.frankheijden.serverutils.common.ServerUtilsApp;

public class UpdateManager {

    private String downloadedVersion = ServerUtilsApp.VERSION;
    private long lastUpdateCheck = 0;

    public UpdateManager() {}

    public String getDownloadedVersion() {
        return downloadedVersion;
    }

    public void setDownloadedVersion(String downloadedVersion) {
        this.downloadedVersion = downloadedVersion;
    }

    public boolean hasDownloaded() {
        return !downloadedVersion.equals(ServerUtilsApp.VERSION);
    }

    public boolean canRunUpdateCheck() {
        return lastUpdateCheck + 1000 * 60 * 30 <= System.currentTimeMillis();
    }

    public void updateLastUpdateCheck() {
        this.lastUpdateCheck = System.currentTimeMillis();
    }
}
