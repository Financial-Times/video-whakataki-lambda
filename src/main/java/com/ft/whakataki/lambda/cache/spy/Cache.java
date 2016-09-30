package com.ft.whakataki.lambda.cache.spy;

import java.util.List;
import java.util.Map;


public interface Cache<K, V> {


    public void setEvictionTimeout(int seconds);


    public int getEvictionTimeout();


    public boolean add(K k, V v);


    public boolean add(Map<K, V> entries);


    public V get(K key);


    public Map<K, V> get(List<K> keys);


    public Cache<K, V> set(K k, V v);


    public Cache<K, V> set(Map<K, V> entries);


    public Cache<K, V> delete(K k);


    public Cache<K, V> delete(List<K> keys);


    public boolean replace(K k, V v);


    public boolean replace(Map<K, V> entries);


    public Cache<K, V> flushAll();

    public Object destroy();

}

