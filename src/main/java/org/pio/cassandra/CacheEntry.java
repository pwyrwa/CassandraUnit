package org.pio.cassandra;

/**
 * Generic way to return/ store various models to/from cassandra
 *
 * User: pwyrwins
 * Date: 7/28/12
 * Time: 10:47 AM
 */
public class CacheEntry <T> {

    protected final boolean success;
    protected final boolean inCache;
    protected final T model;
    protected final Exception exception;

    protected CacheEntry(boolean success, boolean inCache, T model, Exception ex)
    {
        this.success = success;
        this.inCache = inCache;
        this.model = model;
        this.exception = ex;
    }

    public static <T> CacheEntry<T> notInCache()
    {
        return new CacheEntry<T>(true, false, null, null);
    }

    public static <T> CacheEntry<T> inCache(T model)
    {
        return new CacheEntry<T>(true, true, model, null);
    }

    public static <T> CacheEntry<T> error(Exception e)
    {
        return new CacheEntry<T>(false, false, null, e);
    }

    public static <T> CacheEntry<T> updateSuccessful()
    {
        return new CacheEntry<T>(true, false, null, null);
    }



    public boolean isSuccess() {
        return success;
    }

    public boolean isInCache() {
        return inCache;
    }

    public T getModel() {
        return model;
    }

    public Exception getException() {
        return exception;
    }
}
