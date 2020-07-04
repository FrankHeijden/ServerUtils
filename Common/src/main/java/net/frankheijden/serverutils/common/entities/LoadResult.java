package net.frankheijden.serverutils.common.entities;

public class LoadResult<T> {

    private final T obj;
    private final Result result;

    public LoadResult(T obj, Result result) {
        this.obj = obj;
        this.result = result;
    }

    public LoadResult(T obj) {
        this(obj, Result.SUCCESS);
    }

    public LoadResult(Result result) {
        this(null, result);
    }

    public T get() {
        return obj;
    }

    public Result getResult() {
        return result;
    }

    public boolean isSuccess() {
        return obj != null && result == Result.SUCCESS;
    }
}
