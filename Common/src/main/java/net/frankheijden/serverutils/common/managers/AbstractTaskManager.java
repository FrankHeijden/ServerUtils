package net.frankheijden.serverutils.common.managers;

public abstract class AbstractTaskManager {

    public abstract void runTask(Runnable runnable);

    public abstract void runTaskAsynchronously(Runnable runnable);
}
