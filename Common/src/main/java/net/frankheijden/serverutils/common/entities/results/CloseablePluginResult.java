package net.frankheijden.serverutils.common.entities.results;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CloseablePluginResult<T> extends PluginResult<T> implements Closeable {

    private final List<Closeable> closeables;

    public CloseablePluginResult(String pluginId, Result result) {
        super(pluginId, result);
        this.closeables = Collections.emptyList();
    }

    public CloseablePluginResult(String pluginId, T plugin, Result result, List<Closeable> closeables) {
        super(pluginId, plugin, result);
        this.closeables = closeables;
    }

    /**
     * Attempts to close the closable, essentially wrapping it with try-catch.
     */
    public void tryClose() {
        if (closeables == null) return;
        try {
            close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Closes the closable.
     */
    @Override
    public void close() throws IOException {
        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }
}
