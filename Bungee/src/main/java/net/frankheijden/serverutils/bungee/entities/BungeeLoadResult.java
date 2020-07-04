package net.frankheijden.serverutils.bungee.entities;

import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLoadResult extends LoadResult<Plugin> {

    public BungeeLoadResult(Plugin obj, Result result) {
        super(obj, result);
    }

    public BungeeLoadResult(Plugin obj) {
        super(obj);
    }

    public BungeeLoadResult(Result result) {
        super(result);
    }
}
