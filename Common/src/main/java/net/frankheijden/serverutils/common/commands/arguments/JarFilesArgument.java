package net.frankheijden.serverutils.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import io.leangen.geantyref.TypeToken;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class JarFilesArgument<C extends ServerUtilsAudience<?>> extends CommandArgument<C, File[]> {

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

    public static final class JarFilesParser<C extends ServerUtilsAudience<?>> implements ArgumentParser<C, File[]> {

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
            List<File> files = new ArrayList<>(inputQueue.size());
            while (!inputQueue.isEmpty()) {
                StringBuilder builder = new StringBuilder(inputQueue.peek());
                if (builder.length() == 0) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(
                            "Specified argument is empty"
                    ));
                }
                inputQueue.remove();

                final String pluginFileName;
                if (builder.charAt(0) == '"') {
                    while (!inputQueue.isEmpty()) {
                        if (builder.length() > 1 && builder.charAt(builder.length() - 1) == '"') {
                            break;
                        }
                        builder.append(" ").append(inputQueue.remove());
                    }

                    if (builder.charAt(builder.length() - 1) != '"') {
                        return ArgumentParseResult.failure(new IllegalArgumentException(
                                "Could not find closing '\"' character"
                        ));
                    }
                    pluginFileName = builder.substring(1, builder.length() - 1);
                } else {
                    if (builder.length() > 0) {
                        int lastChar;
                        while (builder.charAt((lastChar = builder.length() - 1)) == '\\' && !inputQueue.isEmpty()) {
                            builder.setCharAt(lastChar, ' ');
                            builder.append(inputQueue.remove());
                        }
                    }
                    pluginFileName = builder.toString();
                }

                if (!pluginFiles.contains(pluginFileName)) {
                    return ArgumentParseResult.failure(new IllegalArgumentException(
                            "Plugin '" + pluginFileName + "' does not exist!"
                    ));
                }

                files.add(new File(plugin.getPluginManager().getPluginsFolder(), pluginFileName));
            }

            return ArgumentParseResult.success(files.toArray(new File[0]));
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
