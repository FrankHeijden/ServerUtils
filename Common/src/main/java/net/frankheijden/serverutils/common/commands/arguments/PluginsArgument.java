package net.frankheijden.serverutils.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import io.leangen.geantyref.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.IntFunction;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class PluginsArgument<C extends ServerUtilsAudience<?>, P> extends CommandArgument<C, P[]> {

    /**
     * Constructs a Plugins argument.
     */
    public PluginsArgument(
            boolean required,
            String name,
            PluginsParser<C, P> parser
    ) {
        super(
                required,
                name,
                parser,
                "",
                new TypeToken<P[]>() {},
                null
        );
    }

    public static final class PluginsParser<C extends ServerUtilsAudience<?>, P> implements ArgumentParser<C, P[]> {

        private final ServerUtilsPlugin<P, ?, C, ?, ?> plugin;
        private final IntFunction<P[]> arrayCreator;
        private final String commandConfigPath;

        public PluginsParser(ServerUtilsPlugin<P, ?, C, ?, ?> plugin, IntFunction<P[]> arrayCreator) {
            this(plugin, arrayCreator, null);
        }

        /**
         * Constructs a new PluginsParser.
         */
        public PluginsParser(
                ServerUtilsPlugin<P, ?, C, ?, ?> plugin,
                IntFunction<P[]> arrayCreator,
                String commandConfigPath
        ) {
            this.plugin = plugin;
            this.arrayCreator = arrayCreator;
            this.commandConfigPath = commandConfigPath;
        }

        @Override
        public ArgumentParseResult<P[]> parse(CommandContext<C> context, Queue<String> inputQueue) {
            if (inputQueue.isEmpty()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(PluginsParser.class, context));
            }

            Set<String> flags = plugin.getCommandsResource().getAllFlagAliases(commandConfigPath + ".flags.force");

            int queueSize = inputQueue.size();
            List<P> plugins = new ArrayList<>(queueSize);
            for (int i = 0; i < queueSize; i++) {
                if (flags.contains(inputQueue.peek())) continue;

                Optional<P> pluginOptional = plugin.getPluginManager().getPlugin(inputQueue.peek());
                if (!pluginOptional.isPresent()) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(
                            "Plugin '" + inputQueue.peek() + "' does not exist!"
                    ));
                }

                inputQueue.remove();
                plugins.add(pluginOptional.get());
            }

            return ArgumentParseResult.success(plugins.stream().toArray(arrayCreator));
        }

        @Override
        public List<String> suggestions(CommandContext<C> context, String input) {
            return plugin.getPluginManager().getPluginNames();
        }

        @Override
        public boolean isContextFree() {
            return true;
        }
    }
}
