package net.frankheijden.serverutils.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frankheijden.serverutils.ServerUtils;
import net.frankheijden.serverutils.config.Messenger;
import net.frankheijden.serverutils.managers.PluginManager;
import net.frankheijden.serverutils.reflection.*;
import net.frankheijden.serverutils.utils.ForwardFilter;
import net.frankheijden.serverutils.utils.ReloadHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.*;

import static net.frankheijden.serverutils.config.Messenger.sendMessage;

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
        plugin.getCommandManager().getRegisteredRootCommands().stream()
                .filter(c -> !ALIASES.contains(c.getCommandName().toLowerCase()))
                .forEach(rootCommand -> {
                    Messenger.sendMessage(sender, "serverutils.help.format",
                            "%command%", rootCommand.getCommandName(),
                            "%subcommand%", "",
                            "%help%", rootCommand.getDescription());

                    rootCommand.getSubCommands().forEach((str, cmd) -> {
                        if (cmd.getPrefSubCommand().isEmpty()) return;
                        Messenger.sendMessage(sender, "serverutils.help.format",
                                "%command%", rootCommand.getCommandName(),
                                "%subcommand%", " " + cmd.getPrefSubCommand(),
                                "%help%", cmd.getHelpText());
                    });
        });
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
        PluginManager.LoadResult loadResult = PluginManager.loadPlugin(jarFile);
        if (!loadResult.isSuccess()) {
            loadResult.getResult().sendTo(sender, "load", jarFile);
            return;
        }

        PluginManager.Result result = PluginManager.enablePlugin(loadResult.getPlugin());
        result.sendTo(sender, "load", jarFile);
    }

    @Subcommand("unloadplugin")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.unloadplugin")
    @Description("Unloads the specified plugin.")
    public void onUnloadPlugin(CommandSender sender, String pluginName) {
        PluginManager.Result result = PluginManager.disablePlugin(pluginName);
        result.sendTo(sender, "unload", pluginName);
    }

    @Subcommand("reloadplugin")
    @CommandCompletion("@plugins")
    @CommandPermission("serverutils.reloadplugin")
    @Description("Reloads a specified plugin.")
    public void onReloadPlugin(CommandSender sender, String pluginName) {
        PluginManager.Result result = PluginManager.reloadPlugin(pluginName);
        result.sendTo(sender, "reload", pluginName);
    }
}
