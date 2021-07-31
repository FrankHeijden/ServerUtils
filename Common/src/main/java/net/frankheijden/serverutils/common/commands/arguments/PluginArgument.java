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
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class PluginArgument<C extends ServerCommandSender<?>, P> extends CommandArgument<C, P> {

    /**
     * Constructs a Plugin argument.
     */
    public PluginArgument(boolean required, String name, ServerUtilsPlugin<P, ?, C, ?, ?> plugin) {
        super(
                required,
                name,
                new PluginParser<>(plugin),
                "",
                new TypeToken<P>() {},
                null
        );
    }

    public static final class PluginParser<C extends ServerCommandSender<?>, P> implements ArgumentParser<C, P> {

        private final ServerUtilsPlugin<P, ?, C, ?, ?> plugin;

        public PluginParser(ServerUtilsPlugin<P, ?, C, ?, ?> plugin) {
            this.plugin = plugin;
        }

        @Override
        public ArgumentParseResult<P> parse(CommandContext<C> context, Queue<String> inputQueue) {
            if (inputQueue.isEmpty()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(PluginParser.class, context));
            }

            Optional<P> pluginOptional = plugin.getPluginManager().getPlugin(inputQueue.peek());
            if (!pluginOptional.isPresent()) {
                return ArgumentParseResult.failure(new IllegalArgumentException(
                        "Plugin '" + inputQueue.peek() + "' does not exist!"
                ));
            }

            inputQueue.remove();
            return ArgumentParseResult.success(pluginOptional.get());
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
