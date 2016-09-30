package com.ft.whakataki.lambda.cache;

import com.ft.whakataki.lambda.config.Configuration;
import org.apache.log4j.Logger;


/**
 * Think about a local JVM level lock (ehcache...)
 *
 * The memcache lock is designed to protect the underlying repository by
 * reducing the number of parallel queries from different load-balanced JVM's.
 */
public class LockHandler {

    private Logger LOG = Logger.getLogger(this.getClass());

    private static final String CACHE_LOCK_SUFFIX = "-lock*";


    private RemoteLockCache remoteLockCache; // memcached

    public LockHandler(Configuration configuration) {
        this.remoteLockCache = new RemoteLockCache(configuration);
    }


    public LockHandler(RemoteLockCache remoteLockCache) {
        this.remoteLockCache = remoteLockCache;
    }

    /**
     * @return True if a lock was obtained, false if a lock is already in place
     */
    public boolean obtainLock(String cacheKey) {
      //  if (localLockCache.obtainLock(cacheKey)) {
            if (remoteLockCache.obtainLock(cacheKey)) {
                LOG.info("Lock obtained for key " + cacheKey);
                return true;
            }
            //else {
            //    releaseLocalLock(cacheKey);
           // }
     //   }
        LOG.info("Lock already applied for key " + cacheKey);
        return false;
    }


    public boolean releaseLock(String cacheKey) {
        LOG.info("Lock released for key " + cacheKey);
        return remoteLockCache.releaseLock(cacheKey);

    }

    /**private String getLockCacheKey(String cacheKey) {
        return cacheKey + "#lock";
    }**/

}

