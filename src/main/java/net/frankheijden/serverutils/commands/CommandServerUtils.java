package net.frankheijden.serverutils.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frankheijden.serverutils.ServerUtils;
import net.frankheijden.serverutils.config.Messenger;
import net.frankheijden.serverutils.managers.*;
import net.frankheijden.serverutils.reflection.*;
import net.frankheijden.serverutils.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;

import static net.frankheijden.serverutils.config.Messenger.sendMessage;
import static net.frankheijden.serverutils.reflection.ReflectionUtils.MINOR;

@CommandAlias("serverutils|su")
public class CommandServerUtils extends BaseCommand {

    private static final Set<String> ALIASES;
    private static final Map<String, ReloadHandler> supportedConfigs;
    static {
        ALIASES = new HashSet<>();
        ALIASES.add("serverutils");
        ALIASES.add("plugins");

        supportedConfigs = new HashMap<>();
        supportedConfigs.put("bukkit", RCraftServer::reloadBukkitConfiguration);
        supportedConfigs.put("commands.yml", RCraftServer::reloadCommandsConfiguration);
        supportedConfigs.put("server-icon.png", RCraftServer::loadIcon);
        supportedConfigs.put("banned-ips.json", RCraftServer::reloadIPBans);
        supportedConfigs.put("banned-players.json", RCraftServer::reloadProfileBans);
    }

    @Dependency
    private ServerUtils plugin;

    public static Set<String> getSupportedConfigs() {
        return supportedConfigs.keySet();
    }

    @Default
    @Subcommand("help")
    @CommandPermission("serverutils.help")
    @Description("Shows a help page with a few commands.")
    public void onHelp(CommandSender sender) {
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

    @Subcommand("reload")
    @CommandPermission("serverutils.reload")
    @Description("Reloads the ServerUtils plugin.")
    public void onReload(CommandSender sender) {
        plugin.reload();
        sendMessage(sender, "serverutils.success",
                "%action%", "reload",
                "%what%", "ServerUtils configurations");
    }

    @Subcommand("reloadconfig")
    @CommandCompletion("@supportedConfigs")
    @CommandPermission("serverutils.reloadconfig")
    @Description("Reloads individual Server configurations.")
    public void onReloadCommands(CommandSender sender, String config) {
        ReloadHandler handler = supportedConfigs.get(config);
        if (handler == null) {
            this.doHelp(sender);
            return;
        }

        String[] replacements = new String[]{ "%action%", "reload", "%what%", config };

        ForwardFilter filter = new ForwardFilter(sender);
        filter.start(Bukkit.getLogger());
        try {
            handler.handle();
            filter.stop(Bukkit.getLogger());

            String path = "serverutils." + (filter.hasWarnings() ? "warning" : "success");
            sendMessage(sender, path, replacements);
        } catch (Exception ex) {
            filter.stop(Bukkit.getLogger());

            ex.printStackTrace();
            sendMessage(sender, "serverutils.error", replacements);
        }
    }

    @Subcommand("loadplugin")
    @CommandCompletion("@pluginJars")
    @CommandPermission("serverutils.loadplugin")
    @Description("Loads the specified jar file as a plugin.")
    public void onLoadPlugin(CommandSender sender, String jarFile) {
        LoadResult loadResult = PluginManager.loadPlugin(jarFile);
        if (!loadResult.isSuccess()) {
            loadResult.getResult().sendTo(sender, "load", jarFile);
            return;
        }

        Result result = PluginManager.enablePlugin(loadResult.getPlugin());
        result.sendTo(sender, "load", jarFile);
    }

    @Subcommand("unloadplugin")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.unloadplugin")
    @Description("Unloads the specified plugin.")
    public void onUnloadPlugin(CommandSender sender, String pluginName) {
        Result result = PluginManager.disablePlugin(pluginName);
        result.sendTo(sender, "unload", pluginName);
    }

    @Subcommand("reloadplugin")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.reloadplugin")
    @Description("Reloads a specified plugin.")
    public void onReloadPlugin(CommandSender sender, String pluginName) {
        Result result = PluginManager.reloadPlugin(pluginName);
        result.sendTo(sender, "reload", pluginName);
    }

    @Subcommand("plugininfo")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.plugininfo")
    @Description("Shows information about the specified plugin.")
    public void onPluginInfo(CommandSender sender, String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            Result.NOT_EXISTS.sendTo(sender, "fetch", pluginName);
            return;
        }

        PluginDescriptionFile description = plugin.getDescription();
        String format = Messenger.getMessage("serverutils.plugininfo.format");
        String listFormatString = Messenger.getMessage("serverutils.plugininfo.list_format");
        String seperator = Messenger.getMessage("serverutils.plugininfo.seperator");
        String lastSeperator = Messenger.getMessage("serverutils.plugininfo.last_seperator");

        ListFormat<String> listFormat = str -> listFormatString.replace("%value%", str);

        Messenger.sendMessage(sender, "serverutils.plugininfo.header");

        FormatBuilder builder = FormatBuilder.create(format)
                .orderedKeys("%key%", "%value%")
                .add("Name", plugin.getName())
                .add("Full Name", description.getFullName())
                .add("Version", description.getVersion());
        if (MINOR >= 13) builder.add( "API Version", description.getAPIVersion());
        builder.add("Website", description.getWebsite())
                .add("Authors", ListBuilder.create(description.getAuthors())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString())
                .add("Description", description.getDescription())
                .add("Main", description.getMain())
                .add("Prefix", description.getPrefix())
                .add("Load Order", description.getLoad().name())
                .add("Load Before", ListBuilder.create(description.getLoadBefore())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString())
                .add("Depend", ListBuilder.create(description.getDepend())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString())
                .add("Soft Depend", ListBuilder.create(description.getSoftDepend())
                        .format(listFormat)
                        .seperator(seperator)
                        .lastSeperator(lastSeperator)
                        .toString());
        if (MINOR >= 15) builder.add("Provides", ListBuilder.create(description.getProvides())
                .format(listFormat)
                .seperator(seperator)
                .lastSeperator(lastSeperator)
                .toString());

        builder.sendTo(sender);
        Messenger.sendMessage(sender, "serverutils.plugininfo.footer");
    }
}
