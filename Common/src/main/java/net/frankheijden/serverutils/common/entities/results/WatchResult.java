package net.frankheijden.serverutils.common.entities.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;

public enum WatchResult implements AbstractResult {

    START,
    CHANGE,
    ALREADY_WATCHING,
    NOT_WATCHING,
    FILE_DELETED,
    DELETED_FILE_IS_CREATED,
    STOPPED;

    private List<String> args = null;

    public WatchResult arg(String arg) {
        return args(Collections.singletonList(arg));
    }

    public WatchResult args(List<String> args) {
        this.args = args;
        return this;
    }

    @Override
    public void sendTo(ServerCommandSender<?> sender, String action, String what) {
        arg(what);
        sendTo(sender);
    }

    /**
     * Sends the result(s) to the console and specified sender.
     */
    public void sendTo(ServerCommandSender<?> sender) {
        String path = "serverutils.watchplugin." + this.name().toLowerCase();
        List<String[]> sendArguments = new ArrayList<>();
        if (args == null || args.isEmpty()) {
            sendArguments.add(new String[0]);
        } else {
            for (String what : args) {
                sendArguments.add(new String[] { "%what%", what });
            }
        }

        MessagesResource messages = ServerUtilsApp.getPlugin().getMessagesResource();
        ServerCommandSender<?> console = ServerUtilsApp.getPlugin().getChatProvider().getConsoleSender();
        for (String[] replacements : sendArguments) {
            messages.sendMessage(sender, path, replacements);
            if (sender.isPlayer()) {
                messages.sendMessage(console, path, replacements);
            }
        }
    }
}
