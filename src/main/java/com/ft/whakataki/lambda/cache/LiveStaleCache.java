package com.ft.whakataki.lambda.cache;

import com.ft.whakataki.lambda.cache.spy.Cache;
import org.apache.log4j.Logger;

public class LiveStaleCache<V> {

    private Logger LOG = Logger.getLogger(this.getClass());

    private final Cache<String, LiveStaleElement<V>> cache;


    private final int defaultLiveCacheTTL;

    public LiveStaleCache(Cache<String, LiveStaleElement<V>> cache, int liveCacheTTL) {
        this.cache = cache;
        this.defaultLiveCacheTTL = liveCacheTTL;
    }

    public V get(String key) throws CachingNotAvailableException {
        try {
            LiveStaleElement<V> entry = cache.get(key);
            if (entry != null && entry.isLive()) {
                LOG.info("Live cache HIT for key " + key);
                if(LOG.isDebugEnabled()){
                    LOG.debug("Live cache returned: " + entry);
                }

                return entry.getData();
            }
            LOG.info("Live cache MISS for key " + key);

            return null;
        }
        catch (IllegalArgumentException e) {
            LOG.info("Caching not available for live key " + key);

            throw new CachingNotAvailableException(e);
        }
    }

    public V getStale(String key) throws CachingNotAvailableException {
        try {
            LiveStaleElement<V> entry = cache.get(key);
            if (entry != null) {
                LOG.info("Stale cache HIT for key " + key);
                if(LOG.isDebugEnabled()) {
                    LOG.debug("Stale cache returned: " + entry);
                }

                return entry.getData();
            }

            LOG.info("Stale cache MISS for key " + key);

            return null;
        }
        catch (IllegalArgumentException e) {
            LOG.info("Caching not available for stale key " + key);

            throw new CachingNotAvailableException(e);
        }
    }

    public void set(String key, V entry, Integer liveTTL) throws CachingNotAvailableException {
        try {
            int liveEvictionInSeconds = defaultLiveCacheTTL;

            if (liveTTL != null) liveEvictionInSeconds = liveTTL;

            cache.set(key, new LiveStaleElement<V>(entry, liveEvictionInSeconds ));
            LOG.info("Set into cache for key " + key);
            if(LOG.isDebugEnabled()) {
                LOG.debug("Set into caches: " + entry);
            }
        }
        catch (IllegalArgumentException e) {
            LOG.info("Caching not available for setting key " + key);

            throw new CachingNotAvailableException(e);
        }
    }

    public void delete(String key) throws CachingNotAvailableException {
        try {
            cache.delete(key);
            LOG.info("Delete from cache for key " + key);

        }
        catch (IllegalArgumentException e) {
            LOG.info("Caching not available for putting key " + key);

            throw new CachingNotAvailableException(e);
        }
    }

    public void destroy() {
        cache.destroy();
    }
}

