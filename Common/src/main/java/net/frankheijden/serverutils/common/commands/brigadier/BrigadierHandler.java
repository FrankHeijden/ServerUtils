package net.frankheijden.serverutils.common.commands.brigadier;

import cloud.commandframework.brigadier.CloudBrigadierManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.leangen.geantyref.TypeToken;
import net.frankheijden.serverutils.common.commands.arguments.JarFilesArgument;
import net.frankheijden.serverutils.common.commands.arguments.PluginsArgument;
import net.frankheijden.serverutils.common.entities.ServerCommandSender;

public class BrigadierHandler<C extends ServerCommandSender<?>, P> {

    private final CloudBrigadierManager<C, ?> brigadierManager;

    public BrigadierHandler(CloudBrigadierManager<C, ?> brigadierManager) {
        this.brigadierManager = brigadierManager;
    }

    /**
     * Registers types with the cloud brigadier manager.
     */
    public void registerTypes() {
        brigadierManager.registerMapping(
                new TypeToken<JarFilesArgument.JarFilesParser<C>>() {},
                builder -> builder
                        .cloudSuggestions()
                        .toConstant(StringArgumentType.greedyString())
        );
        brigadierManager.registerMapping(
                new TypeToken<PluginsArgument.PluginsParser<C, P>>() {},
                builder -> builder
                        .cloudSuggestions()
                        .toConstant(StringArgumentType.greedyString())
        );
    }
}
