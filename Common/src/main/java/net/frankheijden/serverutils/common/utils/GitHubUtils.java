package net.frankheijden.serverutils.common.utils;

import com.google.gson.JsonElement;
import net.frankheijden.serverutils.common.entities.http.GitHubResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class GitHubUtils {

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:77.0)"
            + " Gecko/20100101"
            + " Firefox/77.0";

    private GitHubUtils() {}

    /**
     * Downloads file from a GitHubResponse to a file location.
     */
    public static boolean download(GitHubResponse res, File target) throws IOException {
        if (res.getRateLimit().isRateLimited()) return false;
        try (InputStream is = res.getStream()) {
            if (is == null) return false;
            try (
                    ReadableByteChannel rbc = Channels.newChannel(is);
                    FileOutputStream fos = new FileOutputStream(target)
            ) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                return true;
            }
        }
    }

    public static JsonElement parseJson(GitHubResponse res) throws IOException {
        return FileUtils.parseJson(res.getStream());
    }

    /**
     * Opens a stream to a github url and returns the response.
     */
    public static GitHubResponse stream(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout(10000);
        return GitHubResponse.from(conn);
    }
}
