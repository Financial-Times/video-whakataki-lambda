package com.ft.whakataki.lambda.cache;

/**
 * Created by jem.rayfield on 16/02/2016.
 */
public interface LockCache {

        public boolean obtainLock(String key);

        public boolean releaseLock(String key);

}
