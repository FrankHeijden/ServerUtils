package net.frankheijden.serverutils.common.providers;

import net.frankheijden.serverutils.common.entities.ServerCommandSender;

public abstract class ChatProvider {

    public abstract ServerCommandSender getConsoleSender();

    public abstract String color(String str);

    public abstract void broadcast(String permission, String message);
}
