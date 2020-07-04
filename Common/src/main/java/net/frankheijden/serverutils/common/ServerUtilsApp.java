package net.frankheijden.serverutils.common;

import net.frankheijden.serverutils.common.entities.ServerUtilsPlugin;

public class ServerUtilsApp {

    public static final int BSTATS_METRICS_ID = 7790;
    private static ServerUtilsPlugin plugin;

    public static void init(ServerUtilsPlugin plugin) {
        ServerUtilsApp.plugin = plugin;
    }

    public static ServerUtilsPlugin getPlugin() {
        return plugin;
    }
}
