package net.frankheijden.serverutils.bukkit.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import net.frankheijden.serverutils.bukkit.entities.BukkitCommandSender;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.reflection.RCraftServer;
import net.frankheijden.serverutils.bukkit.reflection.RDedicatedServer;
import net.frankheijden.serverutils.bukkit.utils.ReloadHandler;
import net.frankheijden.serverutils.bukkit.utils.VersionReloadHandler;
import net.frankheijden.serverutils.common.commands.CommandServerUtils;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.ForwardFilter;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitCommandServerUtils extends CommandServerUtils<BukkitPlugin, Plugin, BukkitCommandSender> {

    private static final Map<String, ReloadHandler> supportedConfigs;

    static {
        supportedConfigs = new HashMap<>();
        supportedConfigs.put("bukkit", new VersionReloadHandler(16, RCraftServer::reloadBukkitConfiguration));
        supportedConfigs.put("commands.yml", RCraftServer::reloadCommandsConfiguration);
        supportedConfigs.put("server-icon.png", new VersionReloadHandler(16, RCraftServer::loadIcon));
        supportedConfigs.put("banned-ips.json", new VersionReloadHandler(16, RCraftServer::reloadIpBans));
        supportedConfigs.put("banned-players.json", new VersionReloadHandler(16, RCraftServer::reloadProfileBans));
        supportedConfigs.put("server.properties", new VersionReloadHandler(
                16,
                RDedicatedServer::reloadServerProperties
        ));
    }

    public BukkitCommandServerUtils(BukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(
            CommandManager<BukkitCommandSender> manager,
            cloud.commandframework.Command.Builder<BukkitCommandSender> builder
    ) {
        super.register(manager, builder);

        final List<String> supportedConfigNames = supportedConfigs.entrySet().stream()
                .filter(r -> {
                    if (r instanceof VersionReloadHandler) {
                        VersionReloadHandler reloadHandler = ((VersionReloadHandler) r);
                        return MinecraftReflectionVersion.MINOR <= reloadHandler.getMinecraftVersionMaximum();
                    }
                    return true;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        addArgument(CommandArgument.<BukkitCommandSender, String>ofType(String.class, "config")
                .manager(manager)
                .withSuggestionsProvider((context, s) -> supportedConfigNames)
                .build());

        manager.command(buildSubcommand(builder, "enableplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleEnablePlugin));
        manager.command(buildSubcommand(builder, "disableplugin")
                .argument(getArgument("plugin"))
                .handler(this::handleDisablePlugin));
        manager.command(buildSubcommand(builder, "reloadconfig")
                .argument(getArgument("config"))
                .handler(this::handleReloadConfig));
    }

    private void handleEnablePlugin(CommandContext<BukkitCommandSender> context) {
        BukkitCommandSender sender = context.getSender();
        String pluginName = context.get("pluginName");

        Result result = plugin.getPluginManager().enablePlugin(pluginName);
        result.sendTo(sender, "enabl", pluginName);
    }

    private void handleDisablePlugin(CommandContext<BukkitCommandSender> context) {
        BukkitCommandSender sender = context.getSender();
        String pluginName = context.get("pluginName");

        Result result = plugin.getPluginManager().disablePlugin(pluginName);
        result.sendTo(sender, "disabl", pluginName);
    }

    private void handleReloadConfig(CommandContext<BukkitCommandSender> context) {
        BukkitCommandSender sender = context.getSender();
        String config = context.get("config");

        ReloadHandler handler = supportedConfigs.get(config);
        if (handler == null) {
            plugin.getMessagesResource().sendMessage(
                    sender,
                    "serverutils.reloadconfig.not_exists",
                    "%what%", config
            );
            return;
        }

        if (handler instanceof VersionReloadHandler) {
            VersionReloadHandler versionReloadHandler = (VersionReloadHandler) handler;
            int max = versionReloadHandler.getMinecraftVersionMaximum();

            if (MinecraftReflectionVersion.MINOR > max) {
                plugin.getMessagesResource().sendMessage(
                        sender,
                        "serverutils.reloadconfig.not_supported",
                        "%what%", config
                );
                return;
            }
        }

        String[] replacements = new String[]{ "%action%", "reload", "%what%", config };

        ForwardFilter filter = new ForwardFilter(plugin.getChatProvider(), sender);
        filter.start(Bukkit.getLogger());
        try {
            handler.handle();
            filter.stop(Bukkit.getLogger());

            String path = "serverutils." + (filter.hasWarnings() ? "warning" : "success");
            plugin.getMessagesResource().sendMessage(sender, path, replacements);
        } catch (Exception ex) {
            filter.stop(Bukkit.getLogger());

            ex.printStackTrace();
            plugin.getMessagesResource().sendMessage(sender, "serverutils.error", replacements);
        }
    }

    @Override
    protected FormatBuilder createPluginInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String pluginName
    ) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PluginDescriptionFile description = plugin.getDescription();

        builder.add("Name", plugin.getName())
                .add("Full Name", description.getFullName())
                .add("Version", description.getVersion())
                .add("Website", description.getWebsite())
                .add("Authors", listBuilderFunction.apply(b -> b.addAll(description.getAuthors())))
                .add("Description", description.getDescription())
                .add("Main", description.getMain())
                .add("Prefix", description.getPrefix())
                .add("Load Order", description.getLoad().name())
                .add("Load Before", listBuilderFunction.apply(b -> b.addAll(description.getLoadBefore())))
                .add("Depend", listBuilderFunction.apply(b -> b.addAll(description.getDepend())))
                .add("Soft Depend", listBuilderFunction.apply(b -> b.addAll(description.getSoftDepend())));

        if (MinecraftReflectionVersion.MINOR >= 13) {
            builder.add("API Version", description.getAPIVersion());
        }

        if (MinecraftReflectionVersion.MINOR >= 15) {
            builder.add("Provides", listBuilderFunction.apply(b -> b.addAll(description.getProvides())));
        }

        return builder;
    }

    @Override
    protected FormatBuilder createCommandInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String commandName
    ) {
        Command cmd = BukkitPluginManager.getCommand(commandName);
        builder.add("Name", cmd.getName());

        if (cmd instanceof PluginIdentifiableCommand) {
            builder.add("Plugin", ((PluginIdentifiableCommand) cmd).getPlugin().getName());
        }

        return builder.add("Aliases", listBuilderFunction.apply(b -> b.addAll(cmd.getAliases())))
                .add("Usage", cmd.getUsage())
                .add("Description", cmd.getDescription())
                .add("Label", cmd.getLabel())
                .add("Permission", cmd.getPermission())
                .add("Permission Message", cmd.getPermissionMessage());
    }
}
