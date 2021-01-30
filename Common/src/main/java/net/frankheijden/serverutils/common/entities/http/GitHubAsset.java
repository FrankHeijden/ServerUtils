package net.frankheijden.serverutils.common.entities.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GitHubAsset {

    private final String name;
    private final String downloadUrl;

    public GitHubAsset(String name, String downloadUrl) {
        this.name = name;
        this.downloadUrl = downloadUrl;
    }

    /**
     * Creates a new GitHubAsset from given release url.
     */
    public static GitHubAsset from(JsonObject jsonObject) {
        JsonArray assets = jsonObject.getAsJsonArray("assets");
        if (assets != null && assets.size() > 0) {
            JsonObject assetJson = assets.get(0).getAsJsonObject();
            return new GitHubAsset(
                    assetJson.get("name").getAsString(),
                    assetJson.get("browser_download_url").getAsString()
            );
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
