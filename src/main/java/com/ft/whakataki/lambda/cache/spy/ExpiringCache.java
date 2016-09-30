package com.ft.whakataki.lambda.cache.spy;

import java.io.Serializable;

/**
 * <p>
 * Adds three methods to the Cache to allow users to explicitly
 * set the expiry timeouts in the modification methods:
 * <ul>
 *  <li>add() - for adding new entries to the cache, return boolean representing the success</li>
 *  <li>set() - for adding values, whether they exist in the cache already, or not.</li>
 *  <li>replace() - replace a value, but return false doesn't exist</li>
 * </ul>
 * </p>
 * <p>
 */
public interface ExpiringCache<K,V> extends Cache<K, V> {

    // Only add if does not exist
    public boolean add(K k,int expiryInSeconds, V v);

    // Add regardless
    public Cache<K, V> set(K k,int expiryInSeconds, V v);

    // Only replace if key exists
    public boolean replace(K k,int expiryInSeconds, V v);

    public boolean setOperation(String key, int evictionTimeoutSeconds, Serializable value);
    public boolean deleteOperation(String key);
    public boolean addOperation(K k,int expiryInSeconds, V v);
    public boolean replaceOperation(K k,int expiryInSeconds, V v);


}

