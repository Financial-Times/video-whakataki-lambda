package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.cache.*;
import com.ft.whakataki.lambda.cache.spy.Cache;
import com.ft.whakataki.lambda.cache.spy.LocalCache;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.repo.ThingByUUIDRepository;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.ThingRequest;

import java.util.UUID;


public class CachedThingService implements ThingServiceI {


    protected Cache<String, LiveStaleElement<Response>> liveStaleCache;
    private final String RESOURCE_PATH = "http://api.ft.com/things/";
    private final String CACHE_PREFIX = "thing-uuid-cache-";

    private final Integer thingByIdTimeOutInMs;

    CacheManager cacheManager;
    private Configuration configuration;
    private LockHandler lockHandler;

    public CachedThingService(Configuration configuration, ThingServiceI thingService, Boolean useLocalCache) {
        LiveStaleCache<Response> liveStaleCache = getCache(configuration);
        ThingByUUIDRepository thingByUUIDRepository = new ThingByUUIDRepository(thingService);
        this.configuration = configuration;
        thingByIdTimeOutInMs = configuration.getThingByIdTimeOutInMs();

        lockHandler = new LockHandler(configuration);
        BlazeQueryService blazeQueryService = new BlazeQueryService(thingByUUIDRepository, configuration.getDefaultQueryServiceTimeOutInMs() , CACHE_PREFIX, liveStaleCache, lockHandler);


        cacheManager = new CacheManager(blazeQueryService, liveStaleCache, CACHE_PREFIX, lockHandler);
    }


    private LiveStaleCache<Response> getCache(Configuration configuration) {
        LiveStaleCache<Response> cache;
        if (configuration.getUseLocalCache()) {
            cache = new LiveStaleCache<Response>(new LocalCache<String, LiveStaleElement<Response>>(),configuration.getLiveCacheTTLInSec());
        } else {
            cache = new SpyMemcacheFactory().getLiveStaleCache(configuration);
        }
        return cache;
    }

    public Thing getThingByUUID(String uuid) {
        ThingRequest thingRequest = new ThingRequest();
        thingRequest.setThingURI(uuid);
        thingRequest.setQueryWaitTimeInMs(this.getThingByIdTimeOutInMs());
        thingRequest.setResourcePath(RESOURCE_PATH);
        thingRequest.setLiveCacheTTL(configuration.getLiveCacheTTLInSec());
        return this.cacheManager.retreiveResponse(thingRequest).getContent(Thing.class);
    }


    public Integer getThingByIdTimeOutInMs() {
        return thingByIdTimeOutInMs;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Cache<String, LiveStaleElement<Response>> getLiveStaleCache() {
        return liveStaleCache;
    }

    public void setLiveStaleCache(Cache<String, LiveStaleElement<Response>> liveStaleCache) {
        this.liveStaleCache = liveStaleCache;
    }

}
