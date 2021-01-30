package net.frankheijden.serverutils.common.entities.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class GitHubResponse {

    private final HttpURLConnection connection;
    private final GitHubRateLimit rateLimit;

    public GitHubResponse(HttpURLConnection connection, GitHubRateLimit rateLimit) {
        this.connection = connection;
        this.rateLimit = rateLimit;
    }

    public static GitHubResponse from(HttpURLConnection connection) {
        return new GitHubResponse(connection, GitHubRateLimit.from(connection));
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public GitHubRateLimit getRateLimit() {
        return rateLimit;
    }

    public InputStream getStream() throws IOException {
        int res = connection.getResponseCode();
        return (res >= 200 && res <= 299) ? connection.getInputStream() : connection.getErrorStream();
    }
}
