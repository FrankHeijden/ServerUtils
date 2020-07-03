package net.frankheijden.serverutils.bukkit.reflection;

import static net.frankheijden.serverutils.common.reflection.ReflectionUtils.getDeclaredMethod;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class RPlugin {

    private static Method getFile;

    static {
        try {
            getFile = getDeclaredMethod(JavaPlugin.class, "getFile");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static File getPluginFile(Plugin plugin) throws InvocationTargetException, IllegalAccessException {
        return (File) getFile.invoke(plugin);
    }
}
