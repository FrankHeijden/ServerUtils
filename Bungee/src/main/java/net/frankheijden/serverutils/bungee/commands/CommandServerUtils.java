package net.frankheijden.serverutils.bungee.commands;

import static net.frankheijden.serverutils.common.config.Messenger.sendMessage;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeeLoadResult;
import net.frankheijden.serverutils.bungee.managers.BungeePluginManager;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.bungee.utils.BungeeUtils;
import net.frankheijden.serverutils.common.config.Messenger;
import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.HexUtils;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.common.utils.ListFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

@CommandAlias("bsu|bserverutils")
public class CommandServerUtils extends BaseCommand {

    private static final ProxyServer proxy = ProxyServer.getInstance();
    private static final ServerUtils plugin = ServerUtils.getInstance();
    private static final Set<String> ALIASES;

    static {
        ALIASES = new HashSet<>();
        ALIASES.add("bserverutils");
        ALIASES.add("bplugins");
        ALIASES.add("bungeepl");
    }

    /**
     * Shows the help page to the sender.
     * @param commandSender The sender of the command.
     */
    @Default
    @Subcommand("help")
    @CommandPermission("serverutils.help")
    @Description("Shows a help page with a few commands.")
    public void onHelp(CommandSender commandSender) {
        ServerCommandSender sender = BungeeUtils.wrap(commandSender);
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
    public void onReload(CommandSender sender) {
        plugin.reload();
        sendMessage(BungeeUtils.wrap(sender), "serverutils.success",
                "%action%", "reload",
                "%what%", "ServerUtils Bungee configurations");
    }

    /**
     * Loads the specified plugin on the proxy.
     * @param commandSender The sender of the command.
     * @param jarFile The filename of the plugin in the plugins/ directory.
     */
    @Subcommand("loadplugin")
    @CommandCompletion("@pluginJars")
    @CommandPermission("serverutils.loadplugin")
    @Description("Loads the specified jar file as a plugin.")
    public void onLoadPlugin(CommandSender commandSender, String jarFile) {
        ServerCommandSender sender = BungeeUtils.wrap(commandSender);

        BungeeLoadResult loadResult = BungeePluginManager.get().loadPlugin(jarFile);
        if (!loadResult.isSuccess()) {
            loadResult.getResult().sendTo(sender, "load", jarFile);
            return;
        }

        Plugin plugin = loadResult.get();
        Result result = BungeePluginManager.get().enablePlugin(plugin);
        result.sendTo(sender, "load", plugin.getDescription().getName());
    }

    /**
     * Unloads the specified plugin from the proxy.
     * @param commandSender The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("unloadplugin")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.unloadplugin")
    @Description("Disables and unloads the specified plugin.")
    public void onUnloadPlugin(CommandSender commandSender, String pluginName) {
        CloseableResult result = BungeePluginManager.get().unloadPlugin(pluginName);
        result.getResult().sendTo(BungeeUtils.wrap(commandSender), "unload", pluginName);
        result.tryClose();
    }

    /**
     * Reloads the specified plugin on the proxy.
     * @param sender The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("reloadplugin")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.reloadplugin")
    @Description("Reloads a specified plugin.")
    public void onReloadPlugin(CommandSender sender, String pluginName) {
        // Wacky method to have the resources needed for the reload in memory, in case of a self reload.
        HexUtils utils = new HexUtils();
        Map<String, Object> section = Messenger.getInstance().getConfig().getMap("serverutils");
        String result = BungeePluginManager.get().reloadPlugin(pluginName).toString();

        String msg = (String) section.get(result.toLowerCase());
        if (msg != null && !msg.isEmpty()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', utils.convertHexString(
                    msg.replace("%action%", "reload").replace("%what%", pluginName))));
        }
    }

    /**
     * Shows information about the specified plugin.
     * @param commandSender The sender of the command.
     * @param pluginName The plugin name.
     */
    @Subcommand("plugininfo")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.plugininfo")
    @Description("Shows information about the specified plugin.")
    public void onPluginInfo(CommandSender commandSender, String pluginName) {
        ServerCommandSender sender = BungeeUtils.wrap(commandSender);

        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            Result.NOT_EXISTS.sendTo(sender, "fetch", pluginName);
            return;
        }

        PluginDescription desc = plugin.getDescription();
        String format = Messenger.getMessage("serverutils.plugininfo.format");
        String listFormatString = Messenger.getMessage("serverutils.plugininfo.list_format");
        String seperator = Messenger.getMessage("serverutils.plugininfo.seperator");
        String lastSeperator = Messenger.getMessage("serverutils.plugininfo.last_seperator");

        ListFormat<String> listFormat = str -> listFormatString.replace("%value%", str);

        Messenger.sendMessage(sender, "serverutils.plugininfo.header");

        FormatBuilder builder = FormatBuilder.create(format)
                .orderedKeys("%key%", "%value%")
                .add("Name", desc.getName())
                .add("Version", desc.getVersion())
                .add("Author", desc.getAuthor())
                .add("Description", desc.getDescription())
                .add("Main", desc.getMain())
                .add("File", desc.getFile().getName())
                .add("Depend", ListBuilder.create(desc.getDepends())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString())
                .add("Soft Depend", ListBuilder.create(desc.getSoftDepends())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString());

        builder.sendTo(sender);
        Messenger.sendMessage(sender, "serverutils.plugininfo.footer");
    }

    /**
     * Shows information about a provided command.
     * @param commandSender The sender of the command.
     * @param command The command to lookup.
     */
    @Subcommand("commandinfo")
    @CommandCompletion("@commands")
    @CommandPermission("serverutils.commandinfo")
    @Description("Shows information about the specified command.")
    public void onCommandInfo(CommandSender commandSender, String command) {
        ServerCommandSender sender = BungeeUtils.wrap(commandSender);

        Map<String, Command> commands;
        try {
            commands = RPluginManager.getCommands(proxy.getPluginManager());
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return;
        }

        Command cmd = commands.get(command);
        if (cmd == null) {
            Messenger.sendMessage(sender, "serverutils.commandinfo.not_exists");
            return;
        }

        Plugin plugin;
        try {
            plugin = RPluginManager.getPlugin(proxy.getPluginManager(), cmd);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return;
        }
        if (plugin == null) {
            return;
        }

        String format = Messenger.getMessage("serverutils.commandinfo.format");
        String listFormatString = Messenger.getMessage("serverutils.commandinfo.list_format");
        String seperator = Messenger.getMessage("serverutils.commandinfo.seperator");
        String lastSeperator = Messenger.getMessage("serverutils.commandinfo.last_seperator");

        ListFormat<String> listFormat = str -> listFormatString.replace("%value%", str);

        Messenger.sendMessage(sender, "serverutils.commandinfo.header");
        FormatBuilder builder = FormatBuilder.create(format)
                .orderedKeys("%key%", "%value%")
                .add("Name", cmd.getName())
                .add("Plugin", plugin.getDescription().getName())
                .add("Aliases", ListBuilder.create(cmd.getAliases())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString())
                .add("Permission", cmd.getPermission());

        builder.sendTo(sender);
        Messenger.sendMessage(sender, "serverutils.commandinfo.footer");
    }
}
