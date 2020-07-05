package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.reflection.ReflectionUtils;
import net.frankheijden.serverutils.common.reflection.VersionParam;

public class BungeeReflection extends ReflectionUtils {

    @Override
    public boolean isCompatible(VersionParam param) {
        return true;
    }
}
