package net.frankheijden.serverutils.velocity.entities;

import com.velocitypowered.api.plugin.PluginContainer;
import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;

public class VelocityLoadResult extends LoadResult<PluginContainer> {

    public VelocityLoadResult(PluginContainer obj, Result result) {
        super(obj, result);
    }

    public VelocityLoadResult(PluginContainer obj) {
        super(obj);
    }

    public VelocityLoadResult(Result result) {
        super(result);
    }
}
