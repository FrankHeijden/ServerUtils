package net.frankheijden.serverutils.common.entities.http;

import java.net.HttpURLConnection;

public class GitHubRateLimit {

    private final int limit;
    private final int used;
    private final int remaining;
    private final long reset;

    /**
     * Constructs a new GitHubRateLimit object from given params.
     */
    public GitHubRateLimit(int limit, int used, int remaining, long reset) {
        this.limit = limit;
        this.used = used;
        this.remaining = remaining;
        this.reset = reset;
    }

    /**
     * Creates a new GitHubRateLimit object from the given opened connection.
     */
    public static GitHubRateLimit from(HttpURLConnection connection) {
        return new GitHubRateLimit(
                connection.getHeaderFieldInt("x-ratelimit-limit", 60),
                connection.getHeaderFieldInt("x-ratelimit-used", 0),
                connection.getHeaderFieldInt("x-ratelimit-remaining", 60),
                connection.getHeaderFieldLong("x-ratelimit-reset", System.currentTimeMillis() / 1000)
        );
    }

    public int getLimit() {
        return limit;
    }

    public int getUsed() {
        return used;
    }

    public int getRemaining() {
        return remaining;
    }

    public boolean isRateLimited() {
        return remaining == 0;
    }

    public long getReset() {
        return reset * 1000;
    }
}
