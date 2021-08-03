package net.frankheijden.serverutils.common.entities.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.text.minimessage.Template;

public class PluginWatchResults implements Iterable<PluginWatchResult> {

    private final List<PluginWatchResult> watchResults;

    public PluginWatchResults() {
        this.watchResults = new ArrayList<>();
    }

    public PluginWatchResults add(WatchResult result, Template... templates) {
        return add(new PluginWatchResult(result, templates));
    }

    public PluginWatchResults add(PluginWatchResult watchResult) {
        this.watchResults.add(watchResult);
        return this;
    }

    /**
     * Sends the result(s) to the given sender.
     */
    public void sendTo(ServerUtilsAudience<?> sender) {
        MessagesResource messages = ServerUtilsApp.getPlugin().getMessagesResource();

        for (PluginWatchResult watchResult : watchResults) {
            sender.sendMessage(messages.get(watchResult.getKey()).toComponent(watchResult.getTemplates()));
        }
    }

    @Override
    public Iterator<PluginWatchResult> iterator() {
        return watchResults.iterator();
    }
}
