package com.ft.whakataki.lambda.cache;

import com.ft.whakataki.lambda.cache.spy.Cache;
import com.ft.whakataki.lambda.cache.spy.LocalCache;
import com.ft.whakataki.lambda.config.Configuration;
import org.apache.log4j.Logger;

/**
 * Controls the per-resource locks applied within each memcache instance.
 */
public class RemoteLockCache implements LockCache {


        private Logger LOG = Logger.getLogger(this.getClass());

        private Cache<String, Boolean> lockCache;


        public  RemoteLockCache(Configuration configuration) {
            //this.lockCache = new SpyMemcacheFactory().createLockCache(configuration);
            this.lockCache = new SpyMemcacheFactory().getLockCache(configuration);
        }


        public RemoteLockCache(Cache<String, Boolean> lockCache) {

            this.lockCache = lockCache;
        }

        public synchronized boolean obtainLock(String key) {
            if (lockCache.get(key) != null) {
                LOG.info("Remote lock already applied for key " + key);
                return false;
            }

            lockCache.add(key, true);
            LOG.info("Remote lock obtained for key " + key);
            return true;
        }

        public synchronized boolean releaseLock(String key) {
            if (lockCache.get(key) == null) {
                LOG.info("Remote lock already released for key " + key);
                return false;
            }

            lockCache.delete(key);
            LOG.info("Remote lock released for key " + key);
            return true;
        }



    }

