package net.frankheijden.serverutils.common.events;

public interface PluginEvent<T> {

    enum Stage {
        PRE,
        POST
    }

    T getPlugin();

    Stage getStage();

}
