package net.frankheijden.serverutils.common.entities.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Locale;
import java.util.function.Predicate;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

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
        return from(jsonObject, name -> true);
    }

    /**
     * Creates a new GitHubAsset for given platfrom from given release url.
     */
    public static GitHubAsset from(JsonObject jsonObject, ServerUtilsPlugin.Platform platform) {
        return from(jsonObject, name -> name.toUpperCase(Locale.ENGLISH).contains(platform.name()));
    }

    /**
     * Creates a new GitHubAsset from given release url.
     */
    public static GitHubAsset from(JsonObject jsonObject, Predicate<String> namePredicate) {
        JsonArray assets = jsonObject.getAsJsonArray("assets");
        if (assets != null) {
            for (JsonElement asset : assets) {
                JsonObject assetJson = asset.getAsJsonObject();

                String name = assetJson.get("name").getAsString();
                if (namePredicate.test(name)) {
                    return new GitHubAsset(name, assetJson.get("browser_download_url").getAsString());
                }
            }
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
