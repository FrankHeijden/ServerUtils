package net.frankheijden.serverutils.common.entities.results;

import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.ConfigKey;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.text.minimessage.Template;

public class PluginResult<T> implements AbstractResult {

    private final String pluginId;
    private final T plugin;
    private final Result result;
    private final Template[] templates;

    public PluginResult(String pluginId, Result result) {
        this(pluginId, null, result);
    }

    /**
     * Constructs a new PluginResult.
     */
    public PluginResult(String pluginId, T plugin, Result result, Template... templates) {
        this.pluginId = pluginId;
        this.plugin = plugin;
        this.result = result;
        this.templates = new Template[templates.length + 1];
        this.templates[0] = Template.of("plugin", pluginId);
        System.arraycopy(templates, 0, this.templates, 1, templates.length);
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

    public Template[] getTemplates() {
        return templates;
    }

    public boolean isSuccess() {
        return plugin != null && result == Result.SUCCESS;
    }

    public void sendTo(ServerUtilsAudience<?> sender, ConfigKey successKey) {
        ConfigKey key = isSuccess() ? successKey : result.getKey();
        sender.sendMessage(ServerUtilsApp.getPlugin().getMessagesResource().get(key).toComponent(templates));
    }

    @Override
    public ConfigKey getKey() {
        return result.getKey();
    }
}
