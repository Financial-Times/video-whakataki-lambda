package com.ft.whakataki.lambda.cache;

import com.ft.whakataki.lambda.cache.spy.Cache;
import com.ft.whakataki.lambda.cache.spy.MemcachedClientHolder;
import com.ft.whakataki.lambda.cache.spy.SpyMemcachedCache;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.ft.whakataki.lambda.thing.model.Thing;
import net.spy.memcached.*;
import net.spy.memcached.transcoders.SerializingTranscoder;

import java.io.IOException;

public class SpyMemcacheFactory {

    private static final String AWS_DYNAMIC_CLUSTER = "cache.aws.com";

    private static LiveStaleCache liveStaleCacheInstance;
    private static Cache<String, Boolean> lockCacheInstance;

    public <V> LiveStaleCache<V> getLiveStaleCache(Configuration configuration) {
        if (liveStaleCacheInstance == null)
            liveStaleCacheInstance = createLiveStaleCache(configuration);
        return liveStaleCacheInstance;
    }

    public <V> LiveStaleCache<V> createLiveStaleCache(Configuration configuration) {
        Cache<String, LiveStaleElement<Thing>> cache = createCache(configuration);
        cache.setEvictionTimeout(configuration.getStaleCacheTTLInSec());
        LiveStaleCache<Thing> liveStaleCache = new LiveStaleCache<Thing>(cache, configuration.getLiveCacheTTLInSec());
        return (LiveStaleCache<V>) liveStaleCache;
    }

    public  Cache<String, Boolean> getLockCache(Configuration configuration) {
        if (lockCacheInstance == null)
            lockCacheInstance = createLockCache(configuration);
        return lockCacheInstance;
    }

    public Cache<String, Boolean> createLockCache(Configuration configuration) {
        Cache<String, Boolean> lockCache = createCache(configuration);
        lockCache.setEvictionTimeout(configuration.getStaleCacheTTLInSec());
        return lockCache;
    }

    private Cache createCache(Configuration configuration) {
        ConnectionFactoryBuilder connectionFactoryBuilder = new ConnectionFactoryBuilder();
        if (configuration.getMemcacheServers().contains(AWS_DYNAMIC_CLUSTER)) {
            // We are in AWS and its a dynamic cluster
            connectionFactoryBuilder.setClientMode(ClientMode.Dynamic);
        } else {
            // We are running locally so make sure you have memcached running!
            connectionFactoryBuilder.setClientMode(ClientMode.Static);
        }
        if (configuration.getMemcacheProtocal().equals("BINARY")) {
            connectionFactoryBuilder.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
        } else {
            connectionFactoryBuilder.setProtocol(ConnectionFactoryBuilder.Protocol.TEXT);
        }
        connectionFactoryBuilder.setTranscoder(new SerializingTranscoder());
        connectionFactoryBuilder.setOpTimeout(configuration.getMemcacheOpTimeout());
        connectionFactoryBuilder.setTimeoutExceptionThreshold(configuration.getMemcacheTimeoutExceptionThreshold());
        connectionFactoryBuilder.setHashAlg(DefaultHashAlgorithm.KETAMA_HASH);
        connectionFactoryBuilder.setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT);
        connectionFactoryBuilder.setFailureMode(FailureMode.Redistribute);
        connectionFactoryBuilder.setUseNagleAlgorithm(false);
        MemcachedClient memcachedClient;
        try {
            memcachedClient = new MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses(configuration.getMemcacheServers()));
        } catch (IOException io) {
            throw new ThingLambdaException("Unable to build memcache connection", io);
        }

        MemcachedClientHolder memcachedClientHolder = new MemcachedClientHolder(memcachedClient);

        SpyMemcachedCache cache = new SpyMemcachedCache(memcachedClientHolder,configuration.getMemcacheAccessTimeoutInSec(), configuration.getStaleCacheTTLInSec());

        cache.setModificationTimeoutInMillis(configuration.getMemcacheModificationTimeoutInSec()*1000);
        cache.setSetTimeoutInMillis(configuration.getMemcacheAccessTimeoutInSec()*1000);

        return cache;
    }


}

