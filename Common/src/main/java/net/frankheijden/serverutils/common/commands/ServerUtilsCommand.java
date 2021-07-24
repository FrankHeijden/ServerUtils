package net.frankheijden.serverutils.common.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.permission.Permission;
import java.util.HashMap;
import java.util.Map;
import net.frankheijden.serverutils.common.config.ServerUtilsConfig;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public abstract class ServerUtilsCommand<U extends ServerUtilsPlugin<?, ?, C, ?>, C extends ServerCommandSender<?>> {

    protected final U plugin;
    protected final String commandName;
    protected final ServerUtilsConfig commandConfig;
    protected final Map<String, CommandArgument<C, ?>> arguments;

    protected ServerUtilsCommand(U plugin, String commandName) {
        this.plugin = plugin;
        this.commandName = commandName;
        this.commandConfig = (ServerUtilsConfig) plugin.getCommandsResource().getConfig().get(commandName);
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
     * Parses a subcommand from the config.
     */
    public Command.Builder<C> parseSubcommand(Command.Builder<C> builder, String subcommand) {
        ServerUtilsConfig subcommandConfig = (ServerUtilsConfig) commandConfig.get("subcommands." + subcommand);
        return builder
                .literal(
                        subcommandConfig.getString("main"),
                        ArgumentDescription.of(subcommandConfig.getString("description")),
                        subcommandConfig.getStringList("aliases").toArray(new String[0])
                )
                .permission(subcommandConfig.getString("permission"));
    }

    /**
     * Parses a flag from the config.
     */
    public CommandFlag<Void> parseFlag(String flag) {
        ServerUtilsConfig flagConfig = (ServerUtilsConfig) commandConfig.get("flags." + flag);
        return CommandFlag.newBuilder(flagConfig.getString("main"))
                .withAliases(flagConfig.getStringList("aliases").toArray(new String[0]))
                .withPermission(Permission.of(flagConfig.getString("permission")))
                .withDescription(ArgumentDescription.of(flagConfig.getString("description")))
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
}
