package net.frankheijden.serverutils.bukkit.entities;

import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import org.bukkit.plugin.Plugin;

public class BukkitLoadResult extends LoadResult<Plugin> {

    private BukkitLoadResult(Plugin obj, Result result) {
        super(obj, result);
    }

    public BukkitLoadResult(Plugin obj) {
        this(obj, Result.SUCCESS);
    }

    public BukkitLoadResult(Result result) {
        this(null, result);
    }
}
