package com.ft.whakataki.lambda.thing.service;


import com.ft.whakataki.lambda.cache.*;
import com.ft.whakataki.lambda.cache.spy.Cache;
import com.ft.whakataki.lambda.cache.spy.LocalCache;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.repo.ThingByUUIDJSONLDRepository;
import com.ft.whakataki.lambda.thing.model.ThingRequest;

import java.util.UUID;

public class CachedThingJSONLDService implements ThingJSONLDService {

    protected Cache<String, LiveStaleElement<Response>> liveStaleCache;
    private final String RESOURCE_PATH = "http://api.ft.com/things/";
    private final String CACHE_PREFIX = "thing-cache-";

    private final Integer thingByIdTimeOutInMs;

    CacheManager cacheManager;
    private Configuration configuration;
    private LockHandler lockHandler;

    public CachedThingJSONLDService(Configuration configuration, ThingJSONLDService thingService, Boolean useLocalCache) {
        LiveStaleCache<Response> liveStaleCache = getCache(configuration);

        ThingByUUIDJSONLDRepository thingByUUIDJSONLDRepository = new ThingByUUIDJSONLDRepository(thingService);

        this.configuration = configuration;
        thingByIdTimeOutInMs = configuration.getThingByIdTimeOutInMs();

        lockHandler = new LockHandler(configuration);
        BlazeQueryService blazeQueryService = new BlazeQueryService(thingByUUIDJSONLDRepository, configuration.getDefaultQueryServiceTimeOutInMs() , CACHE_PREFIX, liveStaleCache, lockHandler);

        cacheManager = new CacheManager(blazeQueryService, liveStaleCache, CACHE_PREFIX, lockHandler);
    }


    private LiveStaleCache<Response> getCache(Configuration configuration) {
        LiveStaleCache<Response> cache;
        if (configuration.getUseLocalCache()) {
            cache = new LiveStaleCache<Response>(new LocalCache<String, LiveStaleElement<Response>>(),configuration.getLiveCacheTTLInSec());
        } else {
            //cache = new SpyMemcacheFactory().createLiveStaleCache(configuration);
            cache = new SpyMemcacheFactory().getLiveStaleCache(configuration);
        }
        return cache;
    }

    public String getThingByUUID(String uuid) {
        ThingRequest thingRequest = new ThingRequest();
        thingRequest.setThingURI(uuid);
        thingRequest.setQueryWaitTimeInMs(this.getThingByIdTimeOutInMs());
        thingRequest.setResourcePath(RESOURCE_PATH);
        thingRequest.setLiveCacheTTL(configuration.getLiveCacheTTLInSec());
        return this.cacheManager.retreiveResponse(thingRequest).getContent(String.class);
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
