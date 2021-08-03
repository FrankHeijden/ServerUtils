package net.frankheijden.serverutils.common.entities;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public abstract class ServerUtilsAudience<C> {

    protected final Audience audience;
    protected final C source;

    protected ServerUtilsAudience(Audience audience, C source) {
        this.audience = audience;
        this.source = source;
    }

    public abstract boolean isPlayer();

    public abstract boolean hasPermission(String permission);

    public void sendMessage(Component component) {
        audience.sendMessage(component);
    }

    public C getSource() {
        return this.source;
    }
}
