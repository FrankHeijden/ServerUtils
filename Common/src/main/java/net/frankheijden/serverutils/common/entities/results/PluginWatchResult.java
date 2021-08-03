package net.frankheijden.serverutils.common.entities.results;

import net.frankheijden.serverutils.common.config.ConfigKey;
import net.kyori.adventure.text.minimessage.Template;

public class PluginWatchResult implements AbstractResult {

    private final WatchResult result;
    private final Template[] templates;

    public PluginWatchResult(WatchResult result, Template... templates) {
        this.result = result;
        this.templates = templates;
    }

    public WatchResult getResult() {
        return result;
    }

    public Template[] getTemplates() {
        return templates;
    }

    @Override
    public ConfigKey getKey() {
        return null;
    }
}
