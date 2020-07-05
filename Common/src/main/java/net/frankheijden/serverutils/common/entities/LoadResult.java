package net.frankheijden.serverutils.common.entities;

/**
 * A result which contains a loaded object from a load operation.
 * @param <T> The loaded object type
 */
public class LoadResult<T> {

    private final T obj;
    private final Result result;

    /**
     * Constructs a new LoadResult with an object and a result.
     * @param obj The object of the load operation.
     * @param result The result of the load operation.
     */
    public LoadResult(T obj, Result result) {
        this.obj = obj;
        this.result = result;
    }

    /**
     * Constructs a new LoadResult with an object and a success result.
     * @param obj The object of the load operation.
     */
    public LoadResult(T obj) {
        this(obj, Result.SUCCESS);
    }

    /**
     * Constructs a new LoadResult without a loaded object, just a result.
     * @param result The result of the load operation.
     */
    public LoadResult(Result result) {
        this(null, result);
    }

    /**
     * Retrieves the loaded object.
     * @return The loaded object.
     */
    public T get() {
        return obj;
    }

    /**
     * The result of the LoadResult.
     * @return The result.
     */
    public Result getResult() {
        return result;
    }

    /**
     * Checks whether the result is a success.
     * @return Whether there is success or not.
     */
    public boolean isSuccess() {
        return obj != null && result == Result.SUCCESS;
    }
}
