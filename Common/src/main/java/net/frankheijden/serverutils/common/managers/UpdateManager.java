package net.frankheijden.serverutils.common.managers;

import com.google.gson.JsonElement;
import net.frankheijden.serverutils.common.ServerUtilsApp;

public class UpdateManager {

    private JsonElement lastResponse;
    private String downloadedVersion;

    public UpdateManager() {
        this.lastResponse = null;
        this.downloadedVersion = ServerUtilsApp.VERSION;
    }

    public JsonElement getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(JsonElement lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getDownloadedVersion() {
        return downloadedVersion;
    }

    public void setDownloadedVersion(String downloadedVersion) {
        this.downloadedVersion = downloadedVersion;
    }

    public boolean hasDownloaded() {
        return !downloadedVersion.equals(ServerUtilsApp.VERSION);
    }
}
