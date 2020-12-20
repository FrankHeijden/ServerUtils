package net.frankheijden.serverutils.bukkit.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import dev.frankheijden.minecraftreflection.exceptions.MinecraftReflectionException;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

public class RCommandMap {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of(SimpleCommandMap.class);

    /**
     * Gets the known commands from the given command map.
     * TODO: figure out which version causes method change.
     */
    public static Map<String, Command> getKnownCommands(SimpleCommandMap map) {
        try {
            return reflection.get(map, "knownCommands");
        } catch (MinecraftReflectionException ignored) {
            return reflection.invoke(map, "getKnownCommands");
        }
    }
}
