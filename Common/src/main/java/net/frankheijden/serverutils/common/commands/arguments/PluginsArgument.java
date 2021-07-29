package net.frankheijden.serverutils.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import io.leangen.geantyref.TypeToken;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.IntFunction;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class PluginsArgument<C extends ServerCommandSender<?>, P> extends CommandArgument<C, P[]> {

    /**
     * Constructs a Plugins argument.
     */
    public PluginsArgument(
            boolean required,
            String name,
            ServerUtilsPlugin<P, ?, C, ?, ?> plugin,
            IntFunction<P[]> arrayCreator
    ) {
        super(
                required,
                name,
                new PluginsParser<>(plugin, arrayCreator),
                "",
                new TypeToken<P[]>() {},
                null
        );
    }

    public static final class PluginsParser<C extends ServerCommandSender<?>, P> implements ArgumentParser<C, P[]> {

        private final ServerUtilsPlugin<P, ?, C, ?, ?> plugin;
        private final IntFunction<P[]> arrayCreator;

        public PluginsParser(ServerUtilsPlugin<P, ?, C, ?, ?> plugin, IntFunction<P[]> arrayCreator) {
            this.plugin = plugin;
            this.arrayCreator = arrayCreator;
        }

        @Override
        public ArgumentParseResult<P[]> parse(CommandContext<C> context, Queue<String> inputQueue) {
            if (inputQueue.isEmpty()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(PluginsParser.class, context));
            }

            P[] plugins = arrayCreator.apply(inputQueue.size());
            for (int i = 0; i < plugins.length; i++) {
                Optional<P> pluginOptional = plugin.getPluginManager().getPlugin(inputQueue.peek());
                if (!pluginOptional.isPresent()) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(
                            "Plugin '" + inputQueue.peek() + "' does not exist!"
                    ));
                }

                inputQueue.remove();
                plugins[i] = pluginOptional.get();
            }

            return ArgumentParseResult.success(plugins);
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
