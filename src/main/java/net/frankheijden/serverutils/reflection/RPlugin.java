package net.frankheijden.serverutils.reflection;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.frankheijden.serverutils.reflection.ReflectionUtils.getDeclaredMethod;

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
