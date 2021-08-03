package net.frankheijden.serverutils.common.providers;

import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.kyori.adventure.text.Component;

public interface ServerUtilsAudienceProvider<S> {

    /**
     * Retrieves the console ServerAudience.
     */
    ServerUtilsAudience<S> getConsoleServerAudience();

    /**
     * Converts the given source (specific to impl) to an ServerAudience.
     */
    ServerUtilsAudience<S> get(S source);

    /**
     * Broadcasts a message to all with given permission.
     */
    void broadcast(Component component, String permission);
}
