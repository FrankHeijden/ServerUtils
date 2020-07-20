package net.frankheijden.serverutils.bungee.managers;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import net.md_5.bungee.api.ProxyServer;

public class BungeeTaskManager extends AbstractTaskManager {

    @Override
    public void runTask(Runnable runnable) {
        runTaskAsynchronously(runnable);
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(ServerUtils.getInstance(), runnable);
    }
}
