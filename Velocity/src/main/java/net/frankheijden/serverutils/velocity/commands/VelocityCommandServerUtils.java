package net.frankheijden.serverutils.velocity.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.common.commands.CommandServerUtils;
import net.frankheijden.serverutils.common.utils.KeyValueComponentBuilder;
import net.frankheijden.serverutils.common.utils.ListComponentBuilder;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.frankheijden.serverutils.velocity.entities.VelocityAudience;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;
import net.frankheijden.serverutils.velocity.reflection.RVelocityCommandManager;
import net.kyori.adventure.text.Component;

public class VelocityCommandServerUtils extends CommandServerUtils<VelocityPlugin, PluginContainer, VelocityAudience> {

    public VelocityCommandServerUtils(VelocityPlugin plugin) {
        super(plugin, PluginContainer[]::new);
    }

    @Override
    protected KeyValueComponentBuilder createPluginInfo(
            KeyValueComponentBuilder builder,
            Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
            PluginContainer container
    ) {
        PluginDescription desc = container.getDescription();

        return builder
                .key("Id").value(desc.getId())
                .key("Name").value(desc.getName().orElse(null))
                .key("Version").value(desc.getVersion().orElse("<UNKNOWN>"))
                .key("Author" + (desc.getAuthors().size() == 1 ? "" : "s"))
                .value(listBuilderFunction.apply(b -> b.addAll(desc.getAuthors())))
                .key("Description").value(desc.getDescription().orElse(null))
                .key("URL").value(desc.getUrl().orElse(null))
                .key("Source").value(desc.getSource().map(Path::toString).orElse(null))
                .key("Dependencies")
                .value(listBuilderFunction.apply(b -> b.addAll(desc.getDependencies().stream()
                        .map(PluginDependency::getId)
                        .collect(Collectors.toList()))));
    }

    @Override
    protected KeyValueComponentBuilder createCommandInfo(
            KeyValueComponentBuilder builder,
            Function<Consumer<ListComponentBuilder<String>>, Component> listBuilderFunction,
            String commandName
    ) {
        ServerUtils plugin = ServerUtils.getInstance();
        CommandManager proxyCommandManager = plugin.getProxy().getCommandManager();
        CommandDispatcher<CommandSource> dispatcher = RVelocityCommandManager.getDispatcher(proxyCommandManager);

        builder.key("Name").value(dispatcher.getRoot().getChild(commandName).getName());

        CommandMeta meta = null;
        try {
            meta = proxyCommandManager.getCommandMeta(commandName);
        } catch (Throwable ignored) {
            //
        }

        String pluginName = null;
        if (meta != null) {
            if (meta.getPlugin() != null) {
                pluginName = plugin.getProxy().getPluginManager().fromInstance(meta.getPlugin())
                        .map(c -> c.getDescription().getId())
                        .orElse(null);
            }

            CommandMeta finalMeta = meta;
            builder.key("Aliases").value(listBuilderFunction.apply(b -> b.addAll(finalMeta.getAliases())));
        }

        if (pluginName == null) {
            pluginName = plugin.getPluginCommandManager().findPluginId(commandName).orElse("<UNKNOWN>");
        }

        builder.key("Plugin").value(pluginName);

        return builder;
    }
}
