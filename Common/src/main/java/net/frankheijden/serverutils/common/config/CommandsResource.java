package net.frankheijden.serverutils.common.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

/**
 * The Commands configuration.
 */
public class CommandsResource extends ServerUtilsResource {

    private static final String COMMANDS_RESOURCE = "commands";

    public CommandsResource(ServerUtilsPlugin<?, ?, ?, ?, ?> plugin) {
        super(plugin, COMMANDS_RESOURCE);
    }

    /**
     * Retrieves all flag aliases for the given flag path.
     */
    public Set<String> getAllFlagAliases(String path) {
        Object flagObject = getConfig().get(path);
        if (flagObject instanceof ServerUtilsConfig) {
            ServerUtilsConfig flagConfig = (ServerUtilsConfig) flagObject;

            Set<String> flagAliases = new HashSet<>();
            flagAliases.add("--" + flagConfig.getString("main"));
            for (String alias : flagConfig.getStringList("aliases")) {
                flagAliases.add("-" + alias);
            }

            return flagAliases;
        }

        return Collections.emptySet();
    }

    @Override
    public void migrate(int currentConfigVersion) {

    }
}
