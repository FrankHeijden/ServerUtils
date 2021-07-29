package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.frankheijden.serverutils.common.commands.arguments.JarFilesArgument;
import net.frankheijden.serverutils.common.commands.arguments.PluginArgument;
import net.frankheijden.serverutils.common.commands.arguments.PluginsArgument;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.entities.results.AbstractResult;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResults;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.entities.results.PluginResults;
import net.frankheijden.serverutils.common.entities.results.Result;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.common.utils.ListFormat;

public abstract class CommandServerUtils<U extends ServerUtilsPlugin<P, ?, C, ?, ?>, P, C extends ServerCommandSender<?>>
        extends ServerUtilsCommand<U, C> {

    private final IntFunction<P[]> arrayCreator;

    protected CommandServerUtils(U plugin, IntFunction<P[]> arrayCreator) {
        super(plugin, "serverutils");
        this.arrayCreator = arrayCreator;
    }

    @Override
    public void register(CommandManager<C> manager, Command.Builder<C> builder) {
        addArgument(new JarFilesArgument<>(true, "jarFiles", plugin));
        addArgument(new PluginsArgument<>(true, "plugins", plugin, arrayCreator));
        addArgument(new PluginArgument<>(true, "plugin", plugin));
        addArgument(CommandArgument.<C, String>ofType(String.class, "command")
                .manager(manager)
                .withSuggestionsProvider((context, s) -> new ArrayList<>(plugin.getPluginManager().getCommands()))
                .build());

        manager.command(builder
                .handler(this::handleHelpCommand));
        manager.command(buildSubcommand(builder, "help")
                .handler(this::handleHelpCommand));
        manager.command(buildSubcommand(builder, "reload")
                .handler(this::handleReload));
        manager.command(buildSubcommand(builder, "loadplugin")
                .argument(getArgument("jarFiles"))
                .handler(this::handleLoadPlugin));
        manager.command(buildSubcommand(builder, "unloadplugin")
                .argument(getArgument("plugins"))
                .handler(this::handleUnloadPlugin));
        manager.command(buildSubcommand(builder, "reloadplugin")
                .argument(getArgument("plugins"))
                .handler(this::handleReloadPlugin));
        manager.command(buildSubcommand(builder, "watchplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleWatchPlugin));
        manager.command(buildSubcommand(builder, "unwatchplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleUnwatchPlugin));
        manager.command(buildSubcommand(builder, "plugininfo")
                .argument(getArgument("plugin"))
                .handler(this::handlePluginInfo));
        manager.command(buildSubcommand(builder, "commandinfo")
                .argument(getArgument("command"))
                .handler(this::handleCommandInfo));
    }

    private void handleHelpCommand(CommandContext<C> context) {
        C sender = context.getSender();
        plugin.getMessagesResource().sendMessage(sender, "serverutils.help.header");

        FormatBuilder builder = FormatBuilder.create(plugin.getMessagesResource().getMessage("serverutils.help.format"))
                .orderedKeys("%command%", "%help%");

        ServerUtilsConfig config = (ServerUtilsConfig) plugin.getCommandsResource().getConfig().get("commands");
        for (String commandName : config.getKeys()) {
            ServerUtilsConfig commandConfig = (ServerUtilsConfig) config.get(commandName);
            CommandElement commandElement = parseElement(commandConfig);
            String shortestCommandAlias = determineShortestAlias(commandElement);

            if (commandElement.shouldDisplayInHelp()) {
                builder.add(shortestCommandAlias, commandElement.getDescription().getDescription());
            }

            Object subcommandsObject = commandConfig.get("subcommands");
            if (subcommandsObject instanceof ServerUtilsConfig) {
                ServerUtilsConfig subcommandsConfig = (ServerUtilsConfig) subcommandsObject;

                for (String subcommandName : subcommandsConfig.getKeys()) {
                    ServerUtilsConfig subcommandConfig = (ServerUtilsConfig) subcommandsConfig.get(subcommandName);
                    CommandElement subcommandElement = parseElement(subcommandConfig);
                    if (subcommandElement.shouldDisplayInHelp()) {
                        String shortestSubcommandAlias = determineShortestAlias(subcommandElement);
                        builder.add(
                                shortestCommandAlias + ' ' + shortestSubcommandAlias,
                                subcommandElement.getDescription().getDescription()
                        );
                    }
                }
            }

            Object flagsObject = commandConfig.get("flags");
            if (flagsObject instanceof ServerUtilsConfig) {
                ServerUtilsConfig flagsConfig = (ServerUtilsConfig) flagsObject;

                for (String flagName : flagsConfig.getKeys()) {
                    ServerUtilsConfig flagConfig = (ServerUtilsConfig) flagsConfig.get(flagName);
                    CommandElement flagElement = parseElement(flagConfig);
                    if (flagElement.shouldDisplayInHelp()) {
                        String shortestFlagAlias = determineShortestAlias(flagElement);
                        String flagPrefix = "-" + (flagElement.getMain().equals(shortestFlagAlias) ? "_" : "");
                        builder.add(
                                shortestCommandAlias + ' ' + flagPrefix + shortestFlagAlias,
                                flagElement.getDescription().getDescription()
                        );
                    }
                }
            }
        }
        builder.build().forEach(msg -> plugin.getMessagesResource().sendRawMessage(sender, msg));
        plugin.getMessagesResource().sendMessage(sender, "serverutils.help.footer");
    }

    private String determineShortestAlias(CommandElement element) {
        String shortestAlias = element.getMain();
        for (String alias : element.getAliases()) {
            if (alias.length() < shortestAlias.length()) {
                shortestAlias = alias;
            }
        }
        return shortestAlias;
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
        List<File> jarFiles = Arrays.asList(context.get("jarFiles"));

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        PluginResults<P> loadResults = pluginManager.loadPlugins(jarFiles);
        if (!loadResults.isSuccess()) {
            PluginResult<P> failedResult = loadResults.last();
            failedResult.getResult().sendTo(sender, "load", failedResult.getPluginId());
            return;
        }

        PluginResults<P> enableResults = pluginManager.enablePlugins(loadResults.getPlugins());
        enableResults.sendTo(sender, "load");
    }

    private void handleUnloadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        List<P> plugins = Arrays.asList(context.get("plugins"));

        PluginResults<P> disableResults = plugin.getPluginManager().disablePlugins(plugins);
        for (PluginResult<P> disableResult : disableResults.getResults()) {
            if (!disableResult.isSuccess() && disableResult.getResult() != Result.ALREADY_DISABLED) {
                disableResult.getResult().sendTo(sender, "disabl", disableResult.getPluginId());
                return;
            }
        }

        CloseablePluginResults<P> unloadResults = plugin.getPluginManager().unloadPlugins(plugins);
        unloadResults.tryClose();
        unloadResults.sendTo(sender, "unload");
    }

    private void handleReloadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        List<P> plugins = Arrays.asList(context.get("plugins"));

        PluginResults<P> reloadResult = plugin.getPluginManager().reloadPlugins(plugins);
        reloadResult.sendTo(sender, "reload");
    }

    private void handleWatchPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        P pluginArg = context.get("plugin");

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        String pluginId = pluginManager.getPluginId(pluginArg);

        AbstractResult result = pluginManager.watchPlugin(sender, pluginId);
        result.sendTo(sender, "watch", pluginId);
    }

    private void handleUnwatchPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        P pluginArg = context.get("plugin");

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        String pluginId = pluginManager.getPluginId(pluginArg);

        AbstractResult result = pluginManager.unwatchPlugin(pluginId);
        result.sendTo(sender, "unwatch", pluginId);
    }

    private void handlePluginInfo(CommandContext<C> context) {
        C sender = context.getSender();
        P pluginArg = context.get("plugin");

        createInfo(sender, "plugininfo", pluginArg, this::createPluginInfo);
    }

    protected abstract FormatBuilder createPluginInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            P pluginArg
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

    private <T> void createInfo(C sender, String command, T item, InfoCreator<T> creator) {
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

    private interface InfoCreator<T> {

        FormatBuilder createInfo(
                FormatBuilder builder,
                Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
                T item
        );
    }
}
