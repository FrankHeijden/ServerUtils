package net.frankheijden.serverutils.bukkit.utils;

import dev.frankheijden.minecraftreflection.MinecraftReflectionVersion;

public class VersionReloadHandler implements ReloadHandler {

    private final int minecraftVersionMaximum;
    private final ReloadHandler handler;

    public VersionReloadHandler(int minecraftVersionMaximum, ReloadHandler handler) {
        this.minecraftVersionMaximum = minecraftVersionMaximum;
        this.handler = handler;
    }

    public int getMinecraftVersionMaximum() {
        return minecraftVersionMaximum;
    }

    @Override
    public void handle() throws Exception {
        if (MinecraftReflectionVersion.MINOR > minecraftVersionMaximum) {
            throw new Exception("ReloadHandler is incompatible with version " + MinecraftReflectionVersion.MINOR
                    + ". Maximum version this handler supports is " + minecraftVersionMaximum + ".");
        }
        handler.handle();
    }
}
