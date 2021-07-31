package net.frankheijden.serverutils.velocity.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.frankheijden.serverutils.common.commands.CommandServerUtils;
import net.frankheijden.serverutils.common.utils.FormatBuilder;
import net.frankheijden.serverutils.common.utils.ListBuilder;
import net.frankheijden.serverutils.velocity.ServerUtils;
import net.frankheijden.serverutils.velocity.entities.VelocityCommandSender;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;
import net.frankheijden.serverutils.velocity.reflection.RVelocityCommandManager;

public class VelocityCommandServerUtils
        extends CommandServerUtils<VelocityPlugin, PluginContainer, VelocityCommandSender> {

    public VelocityCommandServerUtils(VelocityPlugin plugin) {
        super(plugin, PluginContainer[]::new);
    }

    @Override
    protected FormatBuilder createPluginInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            PluginContainer container
    ) {
        PluginDescription desc = container.getDescription();

        return builder
                .add("Id", desc.getId())
                .add("Name", desc.getName().orElse(null))
                .add("Version", desc.getVersion().orElse("<UNKNOWN>"))
                .add(
                        "Author" + (desc.getAuthors().size() == 1 ? "" : "s"),
                        listBuilderFunction.apply(b -> b.addAll(desc.getAuthors()))
                )
                .add("Description", desc.getDescription().orElse(null))
                .add("URL", desc.getUrl().orElse(null))
                .add("Source", desc.getSource().map(Path::toString).orElse(null))
                .add(
                        "Dependencies",
                        listBuilderFunction.apply(b -> b.addAll(desc.getDependencies().stream()
                                .map(PluginDependency::getId)
                                .collect(Collectors.toList())))
                );
    }

    @Override
    protected FormatBuilder createCommandInfo(
            FormatBuilder builder,
            Function<Consumer<ListBuilder<String>>, String> listBuilderFunction,
            String commandName
    ) {
        ServerUtils plugin = ServerUtils.getInstance();
        CommandDispatcher<CommandSource> dispatcher = RVelocityCommandManager.getDispatcher(
                plugin.getProxy().getCommandManager()
        );

        return builder
                .add("Name", dispatcher.getRoot().getChild(commandName).getName())
                .add("Plugin", plugin.getPluginCommandManager().findPluginId(commandName).orElse("<UNKNOWN>"));
    }
}
