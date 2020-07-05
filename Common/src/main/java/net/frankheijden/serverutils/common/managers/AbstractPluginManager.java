package net.frankheijden.serverutils.common.managers;

import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.providers.PluginProvider;

import java.io.File;

public abstract class AbstractPluginManager<T> extends PluginProvider<T> {

    public abstract LoadResult<T> loadPlugin(String pluginFile);

    public abstract LoadResult<T> loadPlugin(File file);

    public abstract Result enablePlugin(T plugin);

    public abstract CloseableResult reloadPlugin(String pluginName);

    public abstract CloseableResult reloadPlugin(T plugin);

    public abstract CloseableResult unloadPlugin(String pluginName);

    public abstract CloseableResult unloadPlugin(T plugin);
}
