package net.frankheijden.serverutils.common.entities.results;

import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.ConfigKey;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;

public class PluginResult<T> implements AbstractResult {

    private final String pluginId;
    private final T plugin;
    private final Result result;
    private final String[] placeholders;

    public PluginResult(String pluginId, Result result) {
        this(pluginId, null, result);
    }

    /**
     * Constructs a new PluginResult.
     */
    public PluginResult(String pluginId, T plugin, Result result, String... placeholders) {
        this.pluginId = pluginId;
        this.plugin = plugin;
        this.result = result;
        this.placeholders = new String[placeholders.length + 2];
        this.placeholders[0] = "plugin";
        this.placeholders[1] = pluginId;
        System.arraycopy(placeholders, 0, this.placeholders, 2, placeholders.length);
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

    public String[] getPlaceholders() {
        return placeholders;
    }

    public boolean isSuccess() {
        return plugin != null && result == Result.SUCCESS;
    }

    public void sendTo(ServerUtilsAudience<?> sender, ConfigKey successKey) {
        ConfigKey key = isSuccess() ? successKey : result.getKey();
        sender.sendMessage(ServerUtilsApp.getPlugin().getMessagesResource().get(key).toComponent(placeholders));
    }

    @Override
    public ConfigKey getKey() {
        return result.getKey();
    }
}
