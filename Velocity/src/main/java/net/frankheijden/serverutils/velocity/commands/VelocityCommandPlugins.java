package net.frankheijden.serverutils.velocity.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.frankheijden.serverutils.common.commands.CommandPlugins;
import net.frankheijden.serverutils.velocity.entities.VelocityCommandSender;
import net.frankheijden.serverutils.velocity.entities.VelocityPlugin;

public class VelocityCommandPlugins extends CommandPlugins<
        VelocityPlugin,
        PluginContainer,
        ScheduledTask,
        VelocityCommandSender,
        CommandSource
        > {

    public VelocityCommandPlugins(VelocityPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void register(
            CommandManager<VelocityCommandSender> manager,
            Command.Builder<VelocityCommandSender> builder
    ) {
        manager.command(builder
                .flag(parseFlag("version"))
                .handler(this::handlePlugins));
    }

    @Override
    protected void handlePlugins(CommandContext<VelocityCommandSender> context) {
        VelocityCommandSender sender = context.getSender();
        boolean hasVersionFlag = context.flags().contains("version");

        handlePlugins(sender, plugin.getPluginManager().getPluginsSorted(), container -> {
            PluginDescription description = container.getDescription();

            String message = plugin.getMessagesResource().getMessage(
                    "serverutils.plugins.format",
                    "%plugin%", description.getId()
            );

            if (hasVersionFlag) {
                message += plugin.getMessagesResource().getMessage(
                        "serverutils.plugins.version",
                        "%version%", description.getVersion().orElse("<UNKNOWN>")
                );
            }

            return message;
        });
    }
}
