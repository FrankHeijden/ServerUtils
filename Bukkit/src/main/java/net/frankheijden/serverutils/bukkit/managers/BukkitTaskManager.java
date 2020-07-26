package net.frankheijden.serverutils.bukkit.managers;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class BukkitTaskManager extends AbstractTaskManager<BukkitTask> {

    public BukkitTaskManager() {
        super(BukkitTask::cancel);
    }

    @Override
    protected BukkitTask runTaskImpl(Runnable runnable) {
        return Bukkit.getScheduler().runTask(ServerUtils.getInstance(), runnable);
    }

    @Override
    protected BukkitTask runTaskAsynchronouslyImpl(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(ServerUtils.getInstance(), runnable);
    }

    @Override
    public void cancelTask(BukkitTask task) {
        task.cancel();
    }
}
