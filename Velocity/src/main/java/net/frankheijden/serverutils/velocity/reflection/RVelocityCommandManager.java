package net.frankheijden.serverutils.velocity.reflection;

import com.mojang.brigadier.CommandDispatcher;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import dev.frankheijden.minecraftreflection.MinecraftReflection;

public class RVelocityCommandManager {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("com.velocitypowered.proxy.command.VelocityCommandManager");

    private RVelocityCommandManager() {}

    public static CommandDispatcher<CommandSource> getDispatcher(CommandManager manager) {
        return reflection.get(manager, "dispatcher");
    }
}
