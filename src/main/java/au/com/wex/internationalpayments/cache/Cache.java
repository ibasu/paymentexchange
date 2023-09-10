package au.com.wex.internationalpayments.cache;

public interface Cache<T> {

    T retrieveCache();

    void addToCache(T t);
}
