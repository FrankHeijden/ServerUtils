package net.frankheijden.serverutils.common.entities;

public abstract class AbstractTask implements Runnable {

    public abstract void cancel();
}
