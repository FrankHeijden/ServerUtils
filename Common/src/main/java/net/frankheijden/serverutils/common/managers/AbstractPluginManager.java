package net.frankheijden.serverutils.common.managers;

import java.io.File;

import net.frankheijden.serverutils.common.entities.CloseableResult;
import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.providers.PluginProvider;

public abstract class AbstractPluginManager<T> extends PluginProvider<T> {

    public abstract LoadResult<T> loadPlugin(String pluginFile);

    public abstract LoadResult<T> loadPlugin(File file);

    public abstract Result enablePlugin(T plugin);

    public abstract Result reloadPlugin(String pluginName);

    public abstract Result reloadPlugin(T plugin);

    public abstract CloseableResult unloadPlugin(String pluginName);

    public abstract CloseableResult unloadPlugin(T plugin);
}
