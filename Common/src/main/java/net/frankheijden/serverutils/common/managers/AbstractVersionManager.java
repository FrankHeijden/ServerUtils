package net.frankheijden.serverutils.common.managers;

public abstract class AbstractVersionManager {

    private final String currentVersion;
    private String downloadedVersion;

    /**
     * Creates a new VersionManager instance.
     * Used for automatic updating.
     * @param currentVersion The current version of the plugin.
     */
    public AbstractVersionManager(String currentVersion) {
        this.currentVersion = currentVersion;
        this.downloadedVersion = currentVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getDownloadedVersion() {
        return downloadedVersion;
    }

    public boolean hasDownloaded() {
        return !downloadedVersion.equals(currentVersion);
    }

    public boolean isDownloaded(String version) {
        return downloadedVersion.equals(version);
    }

    public void setDownloaded(String version) {
        this.downloadedVersion = version;
    }
}
