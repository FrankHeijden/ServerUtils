package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.reflection.ReflectionUtils;
import net.frankheijden.serverutils.common.reflection.VersionParam;
import org.bukkit.Bukkit;

public class BukkitReflection extends ReflectionUtils {

    public static String NMS;
    public static int MAJOR;
    public static int MINOR;
    public static int PATCH;

    static {
        String bukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
        NMS = bukkitPackage.substring(bukkitPackage.lastIndexOf('.') + 1);

        String[] split = NMS.split("_");
        MAJOR = Integer.parseInt(split[0].substring(1));
        MINOR = Integer.parseInt(split[1]);
        PATCH = Integer.parseInt(split[2].substring(1, 2));
    }

    @Override
    public boolean isCompatible(VersionParam versionParam) {
        return versionParam.min.minor <= MINOR && versionParam.min.patch <= PATCH
                && MINOR <= versionParam.max.minor && PATCH <= versionParam.max.patch;
    }
}
