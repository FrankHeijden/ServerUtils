package net.frankheijden.serverutils.common.entities;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * A result which should be closed when done.
 */
public class CloseableResult implements Closeable {

    private Result result;
    private final List<Closeable> closeables;

    /**
     * Constructs a new closable result.
     * Used for unloading / reloading a plugin.
     * NB: The closable needs to be closed to fully ensure that the old plugin doesn't work anymore!
     * @param result The result of the procedure
     * @param closeables The list of closable's of the procedure.
     */
    public CloseableResult(Result result, List<Closeable> closeables) {
        this.result = result;
        this.closeables = closeables;
    }

    /**
     * Constructs a new closable result with no closable instance.
     * @param result The result of the procedure
     */
    public CloseableResult(Result result) {
        this(result, null);
    }

    /**
     * Constructs a new closable result with a closable instance and success result.
     * @param closeable The closable of the procedure.
     */
    public CloseableResult(Closeable closeable) {
        this(Result.SUCCESS, Collections.singletonList(closeable));
    }

    /**
     * Constructs a new closable result with a closable instance and success result.
     * @param closeables The list of closable's of the procedure.
     */
    public CloseableResult(List<Closeable> closeables) {
        this(Result.SUCCESS, closeables);
    }

    /**
     * Retrieves the result.
     * @return The result.
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the result of this instance.
     * @param result The result to set.
     * @return The current instance.
     */
    public CloseableResult set(Result result) {
        this.result = result;
        return this;
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
     * @throws IOException Iff an I/O error occurred.
     */
    @Override
    public void close() throws IOException {
        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }
}
