package net.frankheijden.serverutils.common.entities;

import java.io.Closeable;
import java.io.IOException;

public class CloseableResult implements Closeable {

    private Result result;
    private final Closeable closeable;

    /**
     * Constructs a new closable result.
     * Used for unloading / reloading a plugin.
     * NB: The closable needs to be closed to fully ensure that the old plugin doesn't work anymore!
     * @param result The result of the procedure
     * @param closeable The closable of the procedure.
     */
    public CloseableResult(Result result, Closeable closeable) {
        this.result = result;
        this.closeable = closeable;
    }

    public CloseableResult(Result result) {
        this(result, null);
    }

    public CloseableResult(Closeable closeable) {
        this(Result.SUCCESS, closeable);
    }

    public Result getResult() {
        return result;
    }

    public CloseableResult set(Result result) {
        this.result = result;
        return this;
    }

    /**
     * Attempts to close the closable, essentially wrapping it with try-catch.
     */
    public void tryClose() {
        if (closeable == null) return;
        try {
            close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        closeable.close();
    }
}
