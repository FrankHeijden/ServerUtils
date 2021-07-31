package net.frankheijden.serverutils.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import io.leangen.geantyref.TypeToken;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class JarFilesArgument<C extends ServerCommandSender<?>> extends CommandArgument<C, File[]> {

    /**
     * Constructs a Jar Files argument.
     */
    public JarFilesArgument(boolean required, String name, ServerUtilsPlugin<?, ?, C, ?, ?> plugin) {
        super(
                required,
                name,
                new JarFilesParser<>(plugin),
                "",
                new TypeToken<File[]>() {},
                null
        );
    }

    public static final class JarFilesParser<C extends ServerCommandSender<?>> implements ArgumentParser<C, File[]> {

        private final ServerUtilsPlugin<?, ?, C, ?, ?> plugin;

        public JarFilesParser(ServerUtilsPlugin<?, ?, C, ?, ?> plugin) {
            this.plugin = plugin;
        }

        @Override
        public ArgumentParseResult<File[]> parse(CommandContext<C> context, Queue<String> inputQueue) {
            if (inputQueue.isEmpty()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(JarFilesParser.class, context));
            }

            Set<String> pluginFiles = new HashSet<>(plugin.getPluginManager().getPluginFileNames());
            File[] files = new File[inputQueue.size()];
            for (int i = 0; i < files.length; i++) {
                if (!pluginFiles.contains(inputQueue.peek())) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(
                            "Plugin '" + inputQueue.peek() + "' does not exist!"
                    ));
                }

                files[i] = new File(plugin.getPluginManager().getPluginsFolder(), inputQueue.remove());
            }

            return ArgumentParseResult.success(files);
        }

        @Override
        public List<String> suggestions(CommandContext<C> context, String input) {
            return plugin.getPluginManager().getPluginFileNames();
        }

        @Override
        public boolean isContextFree() {
            return true;
        }
    }
}
