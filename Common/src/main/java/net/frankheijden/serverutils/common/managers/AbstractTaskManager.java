package net.frankheijden.serverutils.common.managers;

public abstract class AbstractTaskManager {

    public abstract void runTaskAsynchronously(Runnable runnable);
}
