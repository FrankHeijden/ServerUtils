package net.frankheijden.serverutils.common.entities.results;

import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.ConfigKey;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

public enum WatchResult implements AbstractResult {
    START(MessageKey.WATCHPLUGIN_START),
    CHANGE(MessageKey.WATCHPLUGIN_CHANGE),
    ALREADY_WATCHING(MessageKey.WATCHPLUGIN_ALREADY_WATCHING),
    NOT_WATCHING(MessageKey.WATCHPLUGIN_NOT_WATCHING),
    FILE_DELETED(MessageKey.WATCHPLUGIN_FILE_DELETED),
    DELETED_FILE_IS_CREATED(MessageKey.WATCHPLUGIN_DELETED_FILE_IS_CREATED),
    STOPPED(MessageKey.WATCHPLUGIN_STOPPED),
    ;

    private final ConfigKey key;

    WatchResult(ConfigKey key) {
        this.key = key;
    }

    public void sendTo(ServerUtilsAudience<?> sender, Template... templates) {
        Component component = ServerUtilsApp.getPlugin().getMessagesResource().get(key).toComponent(templates);
        sender.sendMessage(component);
    }

    @Override
    public ConfigKey getKey() {
        return key;
    }
}
