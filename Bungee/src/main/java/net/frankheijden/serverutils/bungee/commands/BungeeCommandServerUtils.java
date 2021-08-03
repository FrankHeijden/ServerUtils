package net.frankheijden.serverutils.bungee.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.bungee.entities.BungeeAudience;
import net.frankheijden.serverutils.bungee.entities.BungeePlugin;
import net.frankheijden.serverutils.bungee.reflection.RPluginManager;
import net.frankheijden.serverutils.common.commands.CommandServerUtils;
import net.frankheijden.serverutils.common.utils.KeyValueComponentBuilder;
import net.frankheijden.serverutils.common.utils.ListComponentBuilder;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeCommandServerUtils extends CommandServerUtils<BungeePlugin, Plugin, BungeeAudience> {

    public BungeeCommandServerUtils(BungeePlugin plugin) {
        super(plugin, Plugin[]::new);
    }

    @Override
    protected KeyValueComponentBuilder createPluginInfo(
            KeyValueComponentBuilder builder,
            Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
            Plugin bungeePlugin
    ) {
        PluginDescription desc = bungeePlugin.getDescription();

        return builder
                .key("Name").value(desc.getName())
                .key("Version").value(desc.getVersion())
                .key("Author").value(desc.getAuthor())
                .key("Description").value(desc.getDescription())
                .key("Main").value(desc.getMain())
                .key("File").value(desc.getFile().getName())
                .key("Depend").value(listBuilderFunction.apply(b -> b.addAll(desc.getDepends())))
                .key("Soft Depend").value(listBuilderFunction.apply(b -> b.addAll(desc.getSoftDepends())));
    }

    @Override
    protected KeyValueComponentBuilder createCommandInfo(
            KeyValueComponentBuilder builder,
            Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
            String commandName
    ) {
        PluginManager proxyPluginManager = ServerUtils.getInstance().getProxy().getPluginManager();
        Map<String, Command> commands;
        try {
            commands = RPluginManager.getCommands(proxyPluginManager);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            builder.key("Error").value("Please check the console.");
            return builder;
        }

        Command cmd = commands.get(commandName);
        Plugin plugin = RPluginManager.getPlugin(proxyPluginManager, cmd);

        return builder
                .key("Name").value(cmd.getName())
                .key("Plugin").value(plugin == null ? "<UNKNOWN>" : plugin.getDescription().getName())
                .key("Aliases").value(listBuilderFunction.apply(b -> b.addAll(Arrays.asList(cmd.getAliases()))))
                .key("Permission").value(cmd.getPermission());
    }
}
