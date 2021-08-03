package net.frankheijden.serverutils.common.entities.results;

import net.frankheijden.serverutils.common.config.ConfigKey;

public class PluginWatchResult implements AbstractResult {

    private final WatchResult result;
    private final String[] placeholders;

    public PluginWatchResult(WatchResult result, String... placeholders) {
        this.result = result;
        this.placeholders = placeholders;
    }

    public WatchResult getResult() {
        return result;
    }

    public String[] getPlaceholders() {
        return placeholders;
    }

    @Override
    public ConfigKey getKey() {
        return result.getKey();
    }
}
