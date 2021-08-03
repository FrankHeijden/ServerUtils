package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.frankheijden.serverutils.common.commands.arguments.JarFilesArgument;
import net.frankheijden.serverutils.common.commands.arguments.PluginArgument;
import net.frankheijden.serverutils.common.commands.arguments.PluginsArgument;
import net.frankheijden.serverutils.common.config.MessageKey;
import net.frankheijden.serverutils.common.config.MessagesResource;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.entities.results.CloseablePluginResults;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.entities.results.PluginResults;
import net.frankheijden.serverutils.common.entities.results.PluginWatchResults;
import net.frankheijden.serverutils.common.entities.results.Result;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.common.tasks.UpdateCheckerTask;
import net.frankheijden.serverutils.common.utils.ListComponentBuilder;
import net.frankheijden.serverutils.common.utils.KeyValueComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Template;

public abstract class CommandServerUtils<U extends ServerUtilsPlugin<P, ?, C, ?, ?>, P, C extends ServerUtilsAudience<?>>
        extends ServerUtilsCommand<U, C> {

    protected final IntFunction<P[]> arrayCreator;

    protected CommandServerUtils(U plugin, IntFunction<P[]> arrayCreator) {
        super(plugin, "serverutils");
        this.arrayCreator = arrayCreator;
    }

    @Override
    public void register(CommandManager<C> manager, Command.Builder<C> builder) {
        addArgument(new JarFilesArgument<>(true, "jarFiles", plugin));
        addArgument(new PluginsArgument<>(true, "plugins", new PluginsArgument.PluginsParser<>(plugin, arrayCreator)));
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
        manager.command(buildSubcommand(builder, "restart")
                .handler(this::handleRestart));
        manager.command(buildSubcommand(builder, "loadplugin")
                .argument(getArgument("jarFiles"))
                .handler(this::handleLoadPlugin));
        manager.command(buildSubcommand(builder, "unloadplugin")
                .argument(new PluginsArgument<>(
                        true,
                        "plugins",
                        new PluginsArgument.PluginsParser<>(plugin, arrayCreator, getRawPath("unloadplugin"))
                ))
                .handler(this::handleUnloadPlugin));
        manager.command(buildSubcommand(builder, "reloadplugin")
                .argument(new PluginsArgument<>(
                        true,
                        "plugins",
                        new PluginsArgument.PluginsParser<>(plugin, arrayCreator, getRawPath("reloadplugin"))
                ))
                .handler(this::handleReloadPlugin));
        manager.command(buildSubcommand(builder, "watchplugin")
                .argument(new PluginsArgument<>(
                        true,
                        "plugins",
                        new PluginsArgument.PluginsParser<>(plugin, arrayCreator, getRawPath("watchplugin"))
                ))
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

        MessagesResource messages = plugin.getMessagesResource();
        sender.sendMessage(messages.get(MessageKey.HELP_HEADER).toComponent());

        MessagesResource.Message helpFormatMessage = messages.get(MessageKey.HELP_FORMAT);

        ServerUtilsConfig config = (ServerUtilsConfig) plugin.getCommandsResource().getConfig().get("commands");
        for (String commandName : config.getKeys()) {
            ServerUtilsConfig commandConfig = (ServerUtilsConfig) config.get(commandName);
            CommandElement commandElement = parseElement(commandConfig);
            String shortestCommandAlias = determineShortestAlias(commandElement);

            if (commandElement.shouldDisplayInHelp()) {
                sender.sendMessage(helpFormatMessage.toComponent(
                        Template.of("command", shortestCommandAlias),
                        Template.of("help", commandElement.getDescription().getDescription())
                ));
            }

            Object subcommandsObject = commandConfig.get("subcommands");
            if (subcommandsObject instanceof ServerUtilsConfig) {
                ServerUtilsConfig subcommandsConfig = (ServerUtilsConfig) subcommandsObject;

                for (String subcommandName : subcommandsConfig.getKeys()) {
                    ServerUtilsConfig subcommandConfig = (ServerUtilsConfig) subcommandsConfig.get(subcommandName);
                    CommandElement subcommandElement = parseElement(subcommandConfig);
                    if (subcommandElement.shouldDisplayInHelp()) {
                        String shortestSubcommandAlias = determineShortestAlias(subcommandElement);
                        sender.sendMessage(helpFormatMessage.toComponent(
                                Template.of("command", shortestCommandAlias + ' ' + shortestSubcommandAlias),
                                Template.of("help", subcommandElement.getDescription().getDescription())
                        ));
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
                        sender.sendMessage(helpFormatMessage.toComponent(
                                Template.of("command", shortestCommandAlias + ' ' + flagPrefix + shortestFlagAlias),
                                Template.of("help", flagElement.getDescription().getDescription())
                        ));
                    }
                }
            }
        }

        sender.sendMessage(messages.get(MessageKey.HELP_FOOTER).toComponent());
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
        plugin.getMessagesResource().get(MessageKey.RELOAD).sendTo(sender);
    }

    private void handleRestart(CommandContext<C> context) {
        C sender = context.getSender();

        if (checkDependingPlugins(context, sender, Collections.singletonList(plugin.getPlugin()), "restart")) {
            return;
        }

        UpdateCheckerTask.restart(sender);
    }

    private void handleLoadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        List<File> jarFiles = Arrays.asList(context.get("jarFiles"));

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        PluginResults<P> loadResults = pluginManager.loadPlugins(jarFiles);
        if (!loadResults.isSuccess()) {
            PluginResult<P> failedResult = loadResults.last();
            failedResult.sendTo(sender, null);
            return;
        }

        PluginResults<P> enableResults = pluginManager.enablePlugins(loadResults.getPlugins());
        enableResults.sendTo(sender, MessageKey.LOADPLUGIN);
    }

    private void handleUnloadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        List<P> plugins = Arrays.asList(context.get("plugins"));

        if (checkDependingPlugins(context, sender, plugins, "unloadplugin")) {
            return;
        }

        PluginResults<P> disableResults = plugin.getPluginManager().disablePlugins(plugins);
        for (PluginResult<P> disableResult : disableResults.getResults()) {
            if (!disableResult.isSuccess() && disableResult.getResult() != Result.ALREADY_DISABLED) {
                disableResult.sendTo(sender, null);
                return;
            }
        }

        CloseablePluginResults<P> unloadResults = plugin.getPluginManager().unloadPlugins(plugins);
        unloadResults.tryClose();
        unloadResults.sendTo(sender, MessageKey.UNLOADPLUGIN);
    }

    private void handleReloadPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        List<P> plugins = Arrays.asList(context.get("plugins"));

        if (checkDependingPlugins(context, sender, plugins, "reloadplugin")) {
            return;
        }

        if (checkServerUtils(context, sender, plugins)) {
            return;
        }

        PluginResults<P> reloadResults = plugin.getPluginManager().reloadPlugins(plugins);
        reloadResults.sendTo(sender, MessageKey.RELOADPLUGIN_SUCCESS);
    }

    protected boolean checkDependingPlugins(CommandContext<C> context, C sender, List<P> plugins, String subcommand) {
        if (context.flags().contains("force")) return false;

        AbstractPluginManager<P, ?> pluginManager = plugin.getPluginManager();
        MessagesResource messages = plugin.getMessagesResource();

        boolean hasDependingPlugins = false;
        for (P plugin : plugins) {
            String pluginId = pluginManager.getPluginId(plugin);

            List<P> dependingPlugins = pluginManager.getPluginsDependingOn(pluginId);
            if (!dependingPlugins.isEmpty()) {
                TextComponent.Builder builder = Component.text();
                builder.append(messages.get(MessageKey.DEPENDING_PLUGINS_PREFIX).toComponent(
                        Template.of("plugin", pluginId)
                ));
                builder.append(ListComponentBuilder.create(dependingPlugins)
                        .format(p -> messages.get(MessageKey.DEPENDING_PLUGINS_FORMAT).toComponent(
                                Template.of("plugin", pluginManager.getPluginId(p))
                        ))
                        .separator(messages.get(MessageKey.DEPENDING_PLUGINS_SEPARATOR).toComponent())
                        .lastSeparator(messages.get(MessageKey.DEPENDING_PLUGINS_LAST_SEPARATOR).toComponent())
                        .build());
                sender.sendMessage(builder.build());
                hasDependingPlugins = true;
            }
        }

        if (hasDependingPlugins) {
            String flagPath = getRawPath(subcommand) + ".flags.force";
            String forceFlag = plugin.getCommandsResource().getAllFlagAliases(flagPath).stream()
                    .min(Comparator.comparingInt(String::length))
                    .orElse("-f");

            sender.sendMessage(messages.get(MessageKey.DEPENDING_PLUGINS_OVERRIDE).toComponent(
                    Template.of("command", context.getRawInputJoined() + " " + forceFlag)
            ));
        }

        return hasDependingPlugins;
    }

    protected boolean checkServerUtils(CommandContext<C> context, C sender, List<P> plugins) {
        for (P loadedPlugin : plugins) {
            if (plugin.getPlugin() == loadedPlugin) {
                String restartCommand = plugin.getCommandsResource().getAllAliases(getRawPath("restart")).stream()
                        .min(Comparator.comparingInt(String::length))
                        .orElse("restart");
                Component component = plugin.getMessagesResource().get(MessageKey.RELOADPLUGIN_SERVERUTILS).toComponent(
                        Template.of("command", context.getRawInput().peekFirst() + " " + restartCommand)
                );
                sender.sendMessage(component);
                return true;
            }
        }

        return false;
    }

    private void handleWatchPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        List<P> plugins = Arrays.asList(context.get("plugins"));

        if (checkDependingPlugins(context, sender, plugins, "watchplugin")) {
            return;
        }

        if (checkServerUtils(context, sender, plugins)) {
            return;
        }

        PluginWatchResults watchResults = plugin.getWatchManager().watchPlugins(sender, plugins);
        watchResults.sendTo(sender);
    }

    private void handleUnwatchPlugin(CommandContext<C> context) {
        C sender = context.getSender();
        P pluginArg = context.get("plugin");

        String pluginId = plugin.getPluginManager().getPluginId(pluginArg);
        PluginWatchResults watchResults = plugin.getWatchManager().unwatchPluginsAssociatedWith(pluginId);
        watchResults.sendTo(sender);
    }

    private void handlePluginInfo(CommandContext<C> context) {
        C sender = context.getSender();
        P pluginArg = context.get("plugin");

        createInfo(sender, "plugininfo", pluginArg, this::createPluginInfo);
    }

    protected abstract KeyValueComponentBuilder createPluginInfo(
            KeyValueComponentBuilder builder,
            Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
            P pluginArg
    );

    private void handleCommandInfo(CommandContext<C> context) {
        C sender = context.getSender();
        String commandName = context.get("command");

        if (!plugin.getPluginManager().getCommands().contains(commandName)) {
            plugin.getMessagesResource().get(MessageKey.COMMANDINFO_NOT_EXISTS).sendTo(sender);
            return;
        }

        createInfo(sender, "commandinfo", commandName, this::createCommandInfo);
    }

    protected abstract KeyValueComponentBuilder createCommandInfo(
            KeyValueComponentBuilder builder,
            Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
            String commandName
    );

    private <T> void createInfo(C sender, String command, T item, InfoCreator<T> creator) {
        MessagesResource messages = plugin.getMessagesResource();

        MessagesResource.Message formatMessage = messages.get(command + ".format");
        MessagesResource.Message listFormatMessage = messages.get(command + ".list-format");
        Component separator = messages.get(command + ".list-separator").toComponent();
        Component lastSeparator = messages.get(command + ".list-last-separator").toComponent();

        sender.sendMessage(messages.get(command + ".header").toComponent());
        creator.createInfo(
                KeyValueComponentBuilder.create(formatMessage, "key", "value"),
                listBuilderConsumer -> {
                    ListComponentBuilder<String> listBuilder = ListComponentBuilder.<String>create()
                            .format(str -> listFormatMessage.toComponent(Template.of("value", str)))
                            .separator(separator)
                            .lastSeparator(lastSeparator)
                            .emptyValue(null);
                    listBuilderConsumer.accept(listBuilder);
                    return listBuilder.build();
                },
                item
        ).build().forEach(sender::sendMessage);
        sender.sendMessage(messages.get(command + ".footer").toComponent());
    }

    private interface InfoCreator<T> {

        KeyValueComponentBuilder createInfo(
                KeyValueComponentBuilder builder,
                Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
                T item
        );
    }
}
