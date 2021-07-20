package net.frankheijden.serverutils.velocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.common.entities.AbstractResult;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.HexUtils;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.common.utils.ListFormat;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.frankheijden.serverutils.velocity.entities.VelocityLoadResult;
import net.frankheijden.serverutils.velocity.reflection.RVelocityCommandManager;
import net.frankheijden.serverutils.velocity.utils.VelocityUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@CommandAlias("vsu|vserverutils")
public class CommandServerUtils extends BaseCommand {

    private static final Set<String> ALIASES;

    static {
        ALIASES = new HashSet<>();
        ALIASES.add("vserverutils");
        ALIASES.add("vplugins");
        ALIASES.add("velocitypl");
    }

    private final ServerUtils plugin;

    public CommandServerUtils(ServerUtils plugin) {
        this.plugin = plugin;
    }

    /**
     * Shows the help page to the sender.
     * @param source The sender of the command.
     */
    @Default
    @Subcommand("help")
    @CommandPermission("serverutils.help")
    @Description("Shows a help page with a few commands.")
    public void onHelp(CommandSource source) {
        ServerCommandSender sender = VelocityUtils.wrap(source);
        Messenger.sendMessage(sender, "serverutils.help.header");

        FormatBuilder builder = FormatBuilder.create(Messenger.getMessage("serverutils.help.format"))
                .orderedKeys("%command%", "%subcommand%", "%help%");
        plugin.getCommandManager().getRegisteredRootCommands().stream()
                .filter(c -> !ALIASES.contains(c.getCommandName().toLowerCase()))
                .forEach(rootCommand -> {
                    builder.add(rootCommand.getCommandName(), "", rootCommand.getDescription());

                    rootCommand.getSubCommands().forEach((str, cmd) -> {
                        if (cmd.getPrefSubCommand().isEmpty()) return;
                        builder.add(rootCommand.getCommandName(), " " + cmd.getPrefSubCommand(), cmd.getHelpText());
                    });
                });
        builder.sendTo(sender);
        Messenger.sendMessage(sender, "serverutils.help.footer");
    }

    /**
     * Reloads the configurations of ServerUtils.
     * @param sender The sender of the command.
     */
    @Subcommand("reload")
    @CommandPermission("serverutils.reload")
    @Description("Reloads the ServerUtils plugin.")
    public void onReload(CommandSource sender) {
        plugin.reload();
        Messenger.sendMessage(VelocityUtils.wrap(sender), "serverutils.success",
                "%action%", "reload",
                "%what%", "ServerUtils Bungee configurations");
    }

    /**
     * Loads the specified plugin on the proxy.
     * @param source The sender of the command.
     * @param jarFile The filename of the plugin in the plugins/ directory.
     */
    @Subcommand("loadplugin|lp")
    @CommandCompletion("@pluginJars")
    @CommandPermission("serverutils.loadplugin")
    @Description("Loads the specified jar file as a plugin.")
    public void onLoadPlugin(CommandSource source, String jarFile) {
        ServerCommandSender sender = VelocityUtils.wrap(source);

        VelocityLoadResult loadResult = plugin.getPlugin().getPluginManager().loadPlugin(jarFile);
        if (!loadResult.isSuccess()) {
            loadResult.getResult().sendTo(sender, "load", jarFile);
            return;
        }

        PluginContainer container = loadResult.get();
        Result result = plugin.getPlugin().getPluginManager().enablePlugin(container);
        result.sendTo(sender, "load", container.getDescription().getId());
    }

    /**
     * Unloads the specified plugin from the proxy.
     * @param source The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("unloadplugin|up")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.unloadplugin")
    @Description("Disables and unloads the specified plugin.")
    public void onUnloadPlugin(CommandSource source, String pluginName) {
        CloseableResult result = plugin.getPlugin().getPluginManager().unloadPlugin(pluginName);
        result.getResult().sendTo(VelocityUtils.wrap(source), "unload", pluginName);
        result.tryClose();
    }

    /**
     * Reloads the specified plugin on the proxy.
     * @param sender The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("reloadplugin|rp")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.reloadplugin")
    @Description("Reloads a specified plugin.")
    public void onReloadPlugin(CommandSource sender, String pluginName) {
        // Wacky method to have the resources needed for the reload in memory, in case of a self reload.
        HexUtils utils = new HexUtils();
        Map<String, Object> section = Messenger.getInstance().getConfig().getMap("serverutils");
        String result = plugin.getPlugin().getPluginManager().reloadPlugin(pluginName).toString();

        String msg = (String) section.get(result.toLowerCase());
        if (msg != null && !msg.isEmpty()) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(utils.convertHexString(
                    msg.replace("%action%", "reload").replace("%what%", pluginName))));
        }
    }

    /**
     * Watches the given plugin and reloads it when a change is detected to the file.
     * @param source The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("watchplugin|wp")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.watchplugin")
    @Description("Watches the specified plugin for changes.")
    public void onWatchPlugin(CommandSource source, String pluginName) {
        ServerCommandSender sender = VelocityUtils.wrap(source);
        AbstractResult result = plugin.getPlugin().getPluginManager().watchPlugin(sender, pluginName);
        result.sendTo(sender, "watch", pluginName);
    }

    /**
     * Stops watching the given plugin.
     * @param source The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("unwatchplugin|uwp")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.watchplugin")
    @Description("Stops watching the specified plugin for changes.")
    public void onUnwatchPlugin(CommandSource source, String pluginName) {
        AbstractResult result = plugin.getPlugin().getPluginManager().unwatchPlugin(pluginName);
        result.sendTo(VelocityUtils.wrap(source), "unwatch", pluginName);
    }

    /**
     * Shows information about the specified plugin.
     * @param source The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("plugininfo|pi")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.plugininfo")
    @Description("Shows information about the specified plugin.")
    public void onPluginInfo(CommandSource source, String pluginName) {
        ServerCommandSender sender = VelocityUtils.wrap(source);

        Optional<PluginContainer> container = plugin.getProxy().getPluginManager().getPlugin(pluginName);
        if (!container.isPresent()) {
            Result.NOT_EXISTS.sendTo(sender, "fetch", pluginName);
            return;
        }

        PluginDescription desc = container.get().getDescription();
        String format = Messenger.getMessage("serverutils.plugininfo.format");
        String listFormatString = Messenger.getMessage("serverutils.plugininfo.list_format");
        String seperator = Messenger.getMessage("serverutils.plugininfo.seperator");
        String lastSeperator = Messenger.getMessage("serverutils.plugininfo.last_seperator");

        ListFormat<String> listFormat = str -> listFormatString.replace("%value%", str);

        Messenger.sendMessage(sender, "serverutils.plugininfo.header");

        FormatBuilder builder = FormatBuilder.create(format)
                .orderedKeys("%key%", "%value%")
                .add("Id", desc.getId())
                .add("Name", desc.getName().orElse(null))
                .add("Version", desc.getVersion().orElse("<UNKNOWN>"))
                .add("Author" + (desc.getAuthors().size() == 1 ? "" : "s"), ListBuilder.create(desc.getAuthors())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString())
                .add("Description", desc.getDescription().orElse(null))
                .add("URL", desc.getUrl().orElse(null))
                .add("Source", desc.getSource().map(Path::toString).orElse(null))
                .add("Dependencies", ListBuilder.create(desc.getDependencies())
                        .format(d -> listFormat.format(d.getId()))
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString());

        builder.sendTo(sender);
        Messenger.sendMessage(sender, "serverutils.plugininfo.footer");
    }

    /**
     * Shows information about a provided command.
     * @param source The sender of the command.
     * @param command The command to lookup.
     */
    @Subcommand("commandinfo|ci")
    @CommandCompletion("@commands")
    @CommandPermission("serverutils.commandinfo")
    @Description("Shows information about the specified command.")
    public void onCommandInfo(CommandSource source, String command) {
        ServerCommandSender sender = VelocityUtils.wrap(source);

        CommandDispatcher<CommandSource> dispatcher = RVelocityCommandManager.getDispatcher(
                plugin.getProxy().getCommandManager()
        );

        CommandNode<CommandSource> node = dispatcher.getRoot().getChild(command);
        if (node == null) {
            Messenger.sendMessage(sender, "serverutils.commandinfo.not_exists");
            return;
        }

        String format = Messenger.getMessage("serverutils.commandinfo.format");

        Messenger.sendMessage(sender, "serverutils.commandinfo.header");
        FormatBuilder builder = FormatBuilder.create(format)
                .orderedKeys("%key%", "%value%")
                .add("Name", node.getName())
                .add("Plugin", plugin.getPluginCommandManager().findPluginId(command).orElse("<UNKNOWN>"));

        builder.sendTo(sender);
        Messenger.sendMessage(sender, "serverutils.commandinfo.footer");
    }
}
