package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.permission.CommandPermission;
import cloud.commandframework.permission.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.entities.ServerUtilsAudience;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public abstract class ServerUtilsCommand<U extends ServerUtilsPlugin<?, ?, C, ?, ?>, C extends ServerUtilsAudience<?>> {

    protected final U plugin;
    protected final String commandName;
    protected final ServerUtilsConfig commandConfig;
    protected final Map<String, CommandArgument<C, ?>> arguments;

    protected ServerUtilsCommand(U plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
        this.commandConfig = (ServerUtilsConfig) plugin.getCommandsResource().getConfig()
                .get("commands." + commandName);
        this.arguments = new HashMap<>();
    }

    /**
     * Registers commands with the given CommandManager.
     */
    public final void register(CommandManager<C> manager) {
        register(
                manager,
                manager.commandBuilder(
                    applyPrefix(commandConfig.getString("main")),
                    commandConfig.getStringList("aliases").stream()
                            .map(this::applyPrefix)
                            .toArray(String[]::new)
                ).permission(commandConfig.getString("permission"))
        );
    }

    protected abstract void register(CommandManager<C> manager, Command.Builder<C> builder);

    public <A> void addArgument(CommandArgument<C, A> argument) {
        this.arguments.put(argument.getName(), argument);
    }

    public CommandArgument<C, ?> getArgument(String name) {
        return this.arguments.get(name).copy();
    }

    /**
     * Builds a subcommand from the config.
     */
    public void registerSubcommand(
            CommandManager<C> manager,
            Command.Builder<C> builder,
            String subcommandName,
            UnaryOperator<Command.Builder<C>> builderUnaryOperator
    ) {
        CommandElement subcommand = parseSubcommand(subcommandName);

        Stream.concat(
                Stream.of(subcommand.getMain()),
                Arrays.stream(subcommand.getAliases())
        ).map(cmd -> {
            Command.Builder<C> subcommandBuilder = builder
                    .literal(cmd, subcommand.getDescription())
                    .permission(subcommand.getPermission());
            for (CommandElement flagElement : subcommand.getFlags()) {
                subcommandBuilder = subcommandBuilder.flag(createFlag(flagElement));
            }

            return builderUnaryOperator.apply(subcommandBuilder).build();
        }).forEach(manager::command);
    }

    /**
     * Parses a command from the config.
     */
    public CommandElement parseElement(ServerUtilsConfig elementConfig) {
        String main = applyPrefix(elementConfig.getString("main"));
        String descriptionString = elementConfig.getString("description");
        ArgumentDescription description = descriptionString == null ? null : ArgumentDescription.of(descriptionString);
        CommandPermission permission = Permission.of(elementConfig.getString("permission"));
        boolean displayInHelp = elementConfig.getBoolean("display-in-help");
        String[] aliases = elementConfig.getStringList("aliases").stream()
                .map(this::applyPrefix)
                .toArray(String[]::new);

        List<CommandElement> flags = new ArrayList<>();
        Object flagsObject = elementConfig.get("flags");
        if (flagsObject instanceof ServerUtilsConfig) {
            ServerUtilsConfig flagsConfig = ((ServerUtilsConfig) flagsObject);
            for (String flagName : flagsConfig.getKeys()) {
                flags.add(parseElement((ServerUtilsConfig) flagsConfig.get(flagName)));
            }
        }

        return new CommandElement(main, description, permission, displayInHelp, aliases, flags);
    }

    /**
     * Parses a subcommand from the config.
     */
    public CommandElement parseSubcommand(String subcommandName) {
        return parseElement((ServerUtilsConfig) commandConfig.get("subcommands." + subcommandName));
    }

    public String getRawPath(String subcommandName) {
        return "commands." + commandName + ".subcommands." + subcommandName;
    }

    /**
     * Parses a flag from the config.
     */
    public CommandFlag<Void> parseFlag(String flagName) {
        return createFlag(parseElement((ServerUtilsConfig) commandConfig.get("flags." + flagName)));
    }

    /**
     * Creates a flag from a CommandElement.
     */
    public CommandFlag<Void> createFlag(CommandElement flagElement) {
        return CommandFlag.newBuilder(flagElement.getMain())
                .withAliases(flagElement.getAliases())
                .withPermission(flagElement.getPermission())
                .withDescription(flagElement.getDescription())
                .build();
    }

    private String applyPrefix(String str) {
        final String prefixChar;
        switch (plugin.getPlatform()) {
            case BUKKIT:
                prefixChar = "";
                break;
            case BUNGEE:
                prefixChar = "b";
                break;
            case VELOCITY:
                prefixChar = "v";
                break;
            default:
                throw new IllegalArgumentException("Unknown platform: " + plugin.getPlatform().name());
        }

        return str.replace("%prefix%", prefixChar);
    }

    protected static class CommandElement {

        private final String main;
        private final ArgumentDescription description;
        private final CommandPermission permission;
        private final boolean displayInHelp;
        private final String[] aliases;
        private final List<CommandElement> flags;

        public CommandElement(
                String main,
                ArgumentDescription description,
                CommandPermission permission,
                boolean displayInHelp,
                String[] aliases,
                List<CommandElement> flags
        ) {
            this.main = main;
            this.description = description;
            this.permission = permission;
            this.displayInHelp = displayInHelp;
            this.aliases = aliases;
            this.flags = flags;
        }

        public String getMain() {
            return main;
        }

        public ArgumentDescription getDescription() {
            return description;
        }

        public CommandPermission getPermission() {
            return permission;
        }

        public boolean shouldDisplayInHelp() {
            return displayInHelp;
        }

        public String[] getAliases() {
            return aliases;
        }

        public List<CommandElement> getFlags() {
            return flags;
        }
    }
}
