package net.frankheijden.serverutils.bungee.reflection;

import dev.frankheijden.minecraftreflection.MinecraftReflection;
import net.md_5.bungee.api.plugin.PluginDescription;

public class RLibraryLoader {

    private static final MinecraftReflection reflection = MinecraftReflection
            .of("net.md_5.bungee.api.plugin.LibraryLoader");

    private RLibraryLoader() {}

    public static ClassLoader createLoader(Object instance, PluginDescription desc) {
        return instance == null ? null : reflection.invoke(instance, "createLoader", desc);
    }
}
