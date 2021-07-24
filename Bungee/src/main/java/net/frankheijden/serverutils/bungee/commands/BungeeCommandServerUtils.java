package net.frankheijden.serverutils.bungee.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeeCommandSender;
import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.common.commands.CommandServerUtils;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeCommandServerUtils extends CommandServerUtils<BungeePlugin, Plugin, BungeeCommandSender> {

    public BungeeCommandServerUtils(BungeePlugin plugin) {
        super(plugin);
    }

    @Override
    protected FormatBuilder createPluginInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String pluginName
    ) {
        Plugin container = plugin.getPluginManager().getPlugin(pluginName);
        PluginDescription desc = container.getDescription();

        return builder
                .add("Name", desc.getName())
                .add("Version", desc.getVersion())
                .add("Author", desc.getAuthor())
                .add("Description", desc.getDescription())
                .add("Main", desc.getMain())
                .add("File", desc.getFile().getName())
                .add("Depend", listBuilderFunction.apply(b -> b.addAll(desc.getDepends())))
                .add("Soft Depend", listBuilderFunction.apply(b -> b.addAll(desc.getSoftDepends())));
    }

    @Override
    protected FormatBuilder createCommandInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String commandName
    ) {
        PluginManager proxyPluginManager = ServerUtils.getInstance().getProxy().getPluginManager();
        Map<String, Command> commands;
        try {
            commands = RPluginManager.getCommands(proxyPluginManager);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            builder.add("Error", "Please check the console.");
            return builder;
        }

        Command cmd = commands.get(commandName);
        Plugin plugin = RPluginManager.getPlugin(proxyPluginManager, cmd);

        return builder
                .add("Name", cmd.getName())
                .add("Plugin", plugin == null ? "<UNKNOWN>" : plugin.getDescription().getName())
                .add("Aliases", listBuilderFunction.apply(b -> b.addAll(Arrays.asList(cmd.getAliases()))))
                .add("Permission", cmd.getPermission());
    }
}
