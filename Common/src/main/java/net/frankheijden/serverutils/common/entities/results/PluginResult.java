package net.frankheijden.serverutils.common.entities.results;

public class PluginResult<T> {

    private final String pluginId;
    private final T plugin;
    private final Result result;

    public PluginResult(String pluginId, Result result) {
        this(pluginId, null, result);
    }

    /**
     * Constructs a new PluginResult.
     */
    public PluginResult(String pluginId, T plugin, Result result) {
        this.pluginId = pluginId;
        this.plugin = plugin;
        this.result = result;
    }

    public String getPluginId() {
        return pluginId;
    }

    public T getPlugin() {
        return plugin;
    }

    public Result getResult() {
        return result;
    }

    public boolean isSuccess() {
        return plugin != null && result == Result.SUCCESS;
    }
}
