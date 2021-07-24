package net.frankheijden.serverutils.common.config;

import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

/**
 * The Commands configuration.
 */
public class CommandsResource extends ServerUtilsResource {

    private static final String COMMANDS_RESOURCE = "commands";

    public CommandsResource(ServerUtilsPlugin<?, ?, ?, ?> plugin) {
        super(plugin, COMMANDS_RESOURCE);
    }
}
