package net.frankheijden.serverutils.bukkit.managers;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import org.bukkit.Bukkit;

public class BukkitTaskManager extends AbstractTaskManager {

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(ServerUtils.getInstance(), runnable);
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(ServerUtils.getInstance(), runnable);
    }
}
