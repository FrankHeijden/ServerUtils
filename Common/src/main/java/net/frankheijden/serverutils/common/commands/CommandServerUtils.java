package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.frankheijden.serverutils.common.entities.AbstractResult;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.common.utils.ListFormat;

public abstract class CommandServerUtils<
        U extends ServerUtilsPlugin<P, T, C, S>,
        P,
        T,
        C extends ServerCommandSender<S>,
        S
        > extends ServerUtilsCommand<U, P, T, C, S> {

    protected CommandServerUtils(
            U plugin
    ) {
        super(plugin, "serverutils");
    }

    @Override
    public void register(CommandManager<C> manager, Command.Builder<C> builder) {
        final List<String> pluginFileNames = plugin.getPluginManager().getPluginFileNames();
        addArgument(CommandArgument.<C, String>ofType(String.class, "jarFile")
                .manager(manager)
                .withSuggestionsProvider((context, s) -> pluginFileNames)
                .build());

        final List<String> pluginNames = plugin.getPluginManager().getPluginNames();
        addArgument(CommandArgument.<C, String>ofType(String.class, "plugin")
                .manager(manager)
                .withSuggestionsProvider((context, s) -> pluginNames)
                .build());

        final List<String> commandNames = new ArrayList<>(plugin.getPluginManager().getCommands());
        addArgument(CommandArgument.<C, String>ofType(String.class, "command")
                .manager(manager)
                .withSuggestionsProvider((context, s) -> commandNames)
                .build());

        manager.command(builder
                .handler(this::handleHelpCommand));
        manager.command(parseSubcommand(builder, "help")
                .handler(this::handleHelpCommand));
        manager.command(parseSubcommand(builder, "reload")
                .handler(this::handleReload));
        manager.command(parseSubcommand(builder, "loadplugin")
                .argument(getArgument("jarFile"))
                .handler(this::handleLoadPlugin));
        manager.command(parseSubcommand(builder, "unloadplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleUnloadPlugin));
        manager.command(parseSubcommand(builder, "reloadplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleReloadPlugin));
        manager.command(parseSubcommand(builder, "watchplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleWatchPlugin));
        manager.command(parseSubcommand(builder, "unwatchplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleUnwatchPlugin));
        manager.command(parseSubcommand(builder, "plugininfo")
                .argument(getArgument("plugin"))
                .handler(this::handlePluginInfo));
        manager.command(parseSubcommand(builder, "commandinfo")
                .argument(getArgument("command"))
                .handler(this::handleCommandInfo));
    }

    private void handleHelpCommand(CommandContext<C> context) {
        C sender = context.getSender();
        plugin.getMessagesResource().sendMessage(sender, "serverutils.help.header");

        FormatBuilder builder = FormatBuilder.create(plugin.getMessagesResource().getMessage("serverutils.help.format"))
                .orderedKeys("%command%", "%subcommand%", "%help%");

        for (Command<C> command : plugin.getCommands()) {
            List<CommandArgument<C, ?>> arguments = command.getArguments();
            if (arguments.size() < 2) continue;

            String commandName = arguments.get(0).getName();
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < arguments.size(); i++) {
                CommandArgument<C, ?> argument = arguments.get(i);
                sb.append(" ").append(argument.getName());
            }

            String subcommand = sb.toString();
            String description = command.getComponents().get(1).getArgumentDescription().getDescription();

            builder.add(commandName, subcommand, description);
        }
        builder.build().forEach(msg -> plugin.getMessagesResource().sendRawMessage(sender, msg));
        plugin.getMessagesResource().sendMessage(sender, "serverutils.help.footer");
    }

    private void handleReload(CommandContext<C> context) {
        C sender = context.getSender();
        plugin.reload();
        plugin.getMessagesResource().sendMessage(sender, "serverutils.success",
                "%action%", "reload",
                "%what%", "ServerUtils configurations");
    }

    private void handleLoadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        String jarFile = context.get("jarFile");

        AbstractPluginManager<P> pluginManager = plugin.getPluginManager();
        LoadResult<P> loadResult = pluginManager.loadPlugin(jarFile);
        if (!loadResult.isSuccess()) {
            loadResult.getResult().sendTo(sender, "load", jarFile);
            return;
        }

        P loadedPlugin = loadResult.get();
        Result result = pluginManager.enablePlugin(loadedPlugin);
        result.sendTo(sender, "load", pluginManager.getPluginName(loadedPlugin));
    }

    private void handleUnloadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        String pluginName = context.get("plugin");

        CloseableResult result = plugin.getPluginManager().unloadPlugin(pluginName);
        result.getResult().sendTo(sender, "unload", pluginName);
        result.tryClose();
    }

    private void handleReloadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        String pluginName = context.get("plugin");

        Result result = plugin.getPluginManager().reloadPlugin(pluginName);
        result.sendTo(sender, "reload", pluginName);
    }

    private void handleWatchPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        String pluginName = context.get("plugin");

        AbstractResult result = plugin.getPluginManager().watchPlugin(sender, pluginName);
        result.sendTo(sender, "watch", pluginName);
    }

    private void handleUnwatchPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        String pluginName = context.get("plugin");

        AbstractResult result = plugin.getPluginManager().unwatchPlugin(pluginName);
        result.sendTo(sender, "unwatch", pluginName);
    }

    private void handlePluginInfo(CommandContext<C> context) {
        C sender = context.getSender();
        String pluginName = context.get("plugin");

        if (this.plugin.getPluginManager().getPlugin(pluginName) == null) {
            Result.NOT_EXISTS.sendTo(sender, "fetch", pluginName);
            return;
        }

        createInfo(sender, "plugininfo", pluginName, this::createPluginInfo);
    }

    protected abstract FormatBuilder createPluginInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String pluginName
    );

    private void handleCommandInfo(CommandContext<C> context) {
        C sender = context.getSender();
        String commandName = context.get("command");

        if (!plugin.getPluginManager().getCommands().contains(commandName)) {
            plugin.getMessagesResource().sendMessage(sender, "serverutils.commandinfo.not_exists");
            return;
        }

        createInfo(sender, "commandinfo", commandName, this::createCommandInfo);
    }

    protected abstract FormatBuilder createCommandInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String commandName
    );

    private void createInfo(C sender, String command, String item, InfoCreator creator) {
        String messagePrefix = "serverutils." + command;
        String format = plugin.getMessagesResource().getMessage(messagePrefix + ".format");
        String listFormatString = plugin.getMessagesResource().getMessage(messagePrefix + ".list_format");
        String seperator = plugin.getMessagesResource().getMessage(messagePrefix + ".seperator");
        String lastSeperator = plugin.getMessagesResource().getMessage(messagePrefix + ".last_seperator");

        ListFormat<String> listFormat = str -> listFormatString.replace("%value%", str);

        plugin.getMessagesResource().sendMessage(sender, messagePrefix + ".header");
        creator.createInfo(
                FormatBuilder
                        .create(format)
                        .orderedKeys("%key%", "%value%"),
                listBuilderConsumer -> {
                    ListBuilder<String> builder = ListBuilder.<String>create()
                            .format(listFormat)
                            .seperator(seperator)
                            .lastSeperator(lastSeperator);
                    listBuilderConsumer.accept(builder);
                    return builder.toString();
                },
                item
        ).build().forEach(str -> plugin.getMessagesResource().sendRawMessage(sender, str));
        plugin.getMessagesResource().sendMessage(sender, messagePrefix + ".footer");
    }

    private interface InfoCreator {

        FormatBuilder createInfo(
                FormatBuilder builder,
                Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
                String item
        );
    }
}
