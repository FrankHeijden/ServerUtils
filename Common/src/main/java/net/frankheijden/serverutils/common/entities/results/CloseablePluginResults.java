package net.frankheijden.serverutils.common.entities.results;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class CloseablePluginResults<T> extends PluginResults<T> implements Closeable {

    @Override
    public CloseablePluginResults<T> addResult(PluginResult<T> pluginResult) {
        if (!(pluginResult instanceof CloseablePluginResult)) {
            throw new IllegalArgumentException("Not an instance of CloseablePluginResult: " + pluginResult);
        }
        results.add(pluginResult);
        return this;
    }

    @Override
    public CloseablePluginResults<T> addResult(String pluginId, Result result, String... placeholders) {
        super.addResult(pluginId, result, placeholders);
        return this;
    }

    @Override
    public CloseablePluginResults<T> addResult(String pluginId, T plugin, String... placeholders) {
        super.addResult(pluginId, plugin, placeholders);
        return this;
    }

    @Override
    protected CloseablePluginResults<T> addResult(String pluginId, T plugin, Result result, String... placeholders
    ) {
        super.addResult(pluginId, plugin, result, placeholders);
        return this;
    }

    public CloseablePluginResults<T> addResult(
            String pluginId,
            T plugin,
            List<Closeable> closeables,
            String... placeholders
    ) {
        return addResult(new CloseablePluginResult<>(pluginId, plugin, Result.SUCCESS, closeables, placeholders));
    }

    @Override
    public CloseablePluginResult<T> first() {
        PluginResult<T> pluginResult = super.first();
        if (!(pluginResult instanceof CloseablePluginResult)) {
            throw new IllegalArgumentException("Not an instance of CloseablePluginResult: " + pluginResult);
        }
        return (CloseablePluginResult<T>) pluginResult;
    }

    @Override
    public CloseablePluginResult<T> last() {
        PluginResult<T> pluginResult = super.last();
        if (!(pluginResult instanceof CloseablePluginResult)) {
            throw new IllegalArgumentException("Not an instance of CloseablePluginResult: " + pluginResult);
        }
        return (CloseablePluginResult<T>) pluginResult;
    }

    /**
     * Attempts to close the {@link CloseablePluginResult}'s enclosed.
     */
    public void tryClose() {
        try {
            close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        for (PluginResult<T> pluginResult : this) {
            if (pluginResult instanceof CloseablePluginResult) {
                ((CloseablePluginResult<T>) pluginResult).close();
            }
        }
        System.gc();
    }
}
