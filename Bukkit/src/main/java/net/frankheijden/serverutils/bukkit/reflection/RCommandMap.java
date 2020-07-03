package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getDeclaredField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

public class RCommandMap {

    private static Field knownCommands = null;
    private static Method getKnownCommands = null;

    static {
        try {
            try {
                knownCommands = getDeclaredField(SimpleCommandMap.class, "knownCommands");
            } catch (NoSuchFieldException ignored) {
                getKnownCommands = SimpleCommandMap.class.getDeclaredMethod("getKnownCommands");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Command> getKnownCommands(SimpleCommandMap map)
            throws IllegalAccessException, InvocationTargetException {
        return (Map<String, Command>) (knownCommands == null ? getKnownCommands.invoke(map) : knownCommands.get(map));
    }
}
