package net.frankheijden.serverutils.bukkit;

import java.util.ArrayList;
import java.util.List;
import net.frankheijden.serverutils.bukkit.entities.BukkitPlugin;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.bukkit.reflection.RCraftServer;
import net.frankheijden.serverutils.common.ServerUtilsApp;
import net.frankheijden.serverutils.common.utils.StringUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerUtils extends JavaPlugin {

    private static ServerUtils instance;

    private BukkitPlugin plugin;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        this.plugin = new BukkitPlugin(this);
        ServerUtilsApp.init(this, plugin);

        new Metrics(this, ServerUtilsApp.BSTATS_METRICS_ID);
        plugin.enable();
    }

    public static ServerUtils getInstance() {
        return instance;
    }

    public BukkitPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        restoreBukkitPluginCommand();
        plugin.disable();
    }

    public void restoreBukkitPluginCommand() {
        RCraftServer.getCommandMap().register("bukkit", new PluginsCommand("plugins"));
    }

    /**
     * Retrieves the disabled commands from the configuration.
     */
    public List<Command> getDisabledCommands() {
        List<Command> commands = new ArrayList<>();
        for (String cmd : plugin.getConfigResource().getConfig().getStringList("disabled-commands")) {
            String[] split = cmd.split(":");

            Command command;
            if (split.length > 1) {
                String commandString = StringUtils.join(":", split, 1);
                PluginCommand pluginCommand = Bukkit.getPluginCommand(commandString);

                Plugin plugin = getPlugin().getPluginManager().getPlugin(split[0]);
                if (plugin == null) {
                    getLogger().warning("Unknown plugin '" + split[0] + "' in disabled-commands!");
                    continue;
                } else if (pluginCommand == null) {
                    getLogger().warning("Unknown command '" + commandString + "' in disabled-commands!");
                    continue;
                } else if (!plugin.getName().equalsIgnoreCase(pluginCommand.getPlugin().getName())) {
                    // No output here, plugin didn't match!
                    continue;
                }
                command = pluginCommand;
            } else {
                command = BukkitPluginManager.getCommand(split[0]);
                if (command == null) {
                    getLogger().warning("Unknown command '" + split[0] + "' in disabled-commands!");
                    continue;
                }
            }
            commands.add(command);
        }
        return commands;
    }
}
