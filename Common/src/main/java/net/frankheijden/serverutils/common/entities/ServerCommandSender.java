package net.frankheijden.serverutils.common.entities;

public interface ServerCommandSender {

    void sendMessage(String message);

    boolean hasPermission(String permission);
}
