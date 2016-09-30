package com.ft.whakataki.lambda.cache.spy;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LocalCache<K, V> implements Cache<K, V> {


    private ConcurrentHashMap<K, V> internal = new ConcurrentHashMap<K, V>();


    public boolean add(K k, V v) {
        V obj =  internal.putIfAbsent(k, v);
        return (obj==null) ? true : false;

    }


    public boolean add(Map<K, V> entries) {
        boolean result = true;

        for (Map.Entry<K, V> entry : entries.entrySet())
        {
            if(!add(entry.getKey(), entry.getValue()))
            {
                result = false;
            }
        }


        return result;
    }


    public Cache<K, V> delete(Object k) {

        internal.remove(k);

        return this;
    }


    public Cache<K, V> delete(List<K> keys) {

        for (K k : keys)
            internal.remove(k);

        return this;
    }


    public Cache<K, V> flushAll() {
        internal.clear();
        return this;
    }


    public V get(K key) {
        return internal.get(key);
    }


    public Map<K, V> get(List<K> keys) {

        Map<K, V> results = new HashMap<K, V>();

        for (K key : keys)
            results.put(key, internal.get(key));

        return results;
    }


    public boolean replace(K k, V v) {
        V obj= internal.replace(k,v);
        return (obj==null) ? false : true;
    }


    public boolean replace(Map<K, V> entries) {
        boolean replaced = true;
        for (Map.Entry<K, V> entry : entries.entrySet())
        {
            if(!replace(entry.getKey(), entry.getValue()))
            {
                replaced = false;
            }
        }

        return replaced;
    }


    public Cache<K, V> set(K k, V v) {

        internal.put(k, v);
        return this;
    }


    public Cache<K, V> set(Map<K, V> entries) {

        for (Map.Entry<K, V> entry : entries.entrySet())
            internal.put(entry.getKey(), entry.getValue());

        return this;
    }


    public int getEvictionTimeout() {
        return Integer.MIN_VALUE;
    }

    /**
     * Has no affect for this cache implementation
     */
    public void setEvictionTimeout(int seconds) {
        // TODO Auto-generated method stub

    }

    public Object destroy() {
        return this;
    }

}
