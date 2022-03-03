package net.frankheijden.serverutils.bukkit.reflection;

import com.mojang.brigadier.CommandDispatcher;
import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;
import dev.frankheijden.minecraftreflection.exceptions.MinecraftReflectionException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class RCommandDispatcher {

    private static final MinecraftReflection reflection;
    private static final Method getCommandDispatcherMethod;
    private static final Method getDispatcherMethod;

    static {
        if (MinecraftReflectionVersion.MINOR < 13) {
            reflection = null;
            getCommandDispatcherMethod = null;
            getDispatcherMethod = null;
        } else {
            if (MinecraftReflectionVersion.MINOR >= 17) {
                reflection = MinecraftReflection.of("net.minecraft.commands.CommandDispatcher");
            } else {
                reflection = MinecraftReflection.of("net.minecraft.server.%s.CommandDispatcher");
            }

            getCommandDispatcherMethod = Arrays.stream(RMinecraftServer.getReflection().getClazz().getDeclaredMethods())
                    .filter(m -> m.getReturnType().equals(reflection.getClazz()))
                    .findAny()
                    .get();
            getDispatcherMethod = Arrays.stream(getCommandDispatcherMethod.getReturnType().getDeclaredMethods())
                    .filter(m -> CommandDispatcher.class.equals(m.getReturnType()))
                    .findAny()
                    .get();
        }
    }

    public RCommandDispatcher() {}

    /**
     * Retrieves the command dispatcher.
     */
    public static CommandDispatcher<?> getDispatcher() {
        try {
            Object minecraftDispatcher = getCommandDispatcherMethod.invoke(RCraftServer.getConsole());
            return (CommandDispatcher<?>) getDispatcherMethod.invoke(minecraftDispatcher);
        } catch (ReflectiveOperationException ex) {
            throw new MinecraftReflectionException(ex);
        }
    }

    /**
     * Removes commands from the brigadier root node.
     */
    public static void removeCommands(Collection<? extends String> commands) {
        if (MinecraftReflectionVersion.MINOR < 13) return;
        CommandDispatcher<?> dispatcher = getDispatcher();
        for (String command : commands) {
            RCommandNode.removeCommand(dispatcher.getRoot(), command);
        }
    }
}
