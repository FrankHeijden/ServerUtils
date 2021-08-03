package net.frankheijden.serverutils.common.entities.results;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.minimessage.Template;

public class CloseablePluginResult<T> extends PluginResult<T> implements Closeable {

    private final List<Closeable> closeables;

    public CloseablePluginResult(String pluginId, Result result) {
        super(pluginId, result);
        this.closeables = Collections.emptyList();
    }

    public CloseablePluginResult(
            String pluginId,
            T plugin,
            Result result,
            List<Closeable> closeables,
            Template... templates
    ) {
        super(pluginId, plugin, result, templates);
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
