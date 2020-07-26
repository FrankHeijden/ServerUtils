package net.frankheijden.serverutils.bungee.managers;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutils.common.managers.AbstractTaskManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeTaskManager extends AbstractTaskManager<ScheduledTask> {

    public BungeeTaskManager() {
        super(ScheduledTask::cancel);
    }

    @Override
    protected ScheduledTask runTaskImpl(Runnable runnable) {
        return runTaskAsynchronously(runnable);
    }

    @Override
    protected ScheduledTask runTaskAsynchronouslyImpl(Runnable runnable) {
        return ProxyServer.getInstance().getScheduler().runAsync(ServerUtils.getInstance(), runnable);
    }

    @Override
    public void cancelTask(ScheduledTask task) {
        task.cancel();
    }
}
