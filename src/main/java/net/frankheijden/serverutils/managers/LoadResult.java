package net.frankheijden.serverutils.managers;

import org.bukkit.plugin.Plugin;

public class LoadResult {
    private final Plugin plugin;
    private final Result result;

    public LoadResult(Plugin plugin, Result result) {
        this.plugin = plugin;
        this.result = result;
    }

    public LoadResult(Result result) {
        this(null, result);
    }

    public Result getResult() {
        return result;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isSuccess() {
        return plugin != null && result == Result.SUCCESS;
    }
}
