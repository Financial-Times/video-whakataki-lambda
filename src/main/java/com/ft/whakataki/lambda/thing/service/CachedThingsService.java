package com.ft.whakataki.lambda.thing.service;


import com.ft.whakataki.lambda.cache.*;
import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.cache.spy.Cache;
import com.ft.whakataki.lambda.cache.spy.LocalCache;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.Things;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;


public class CachedThingsService implements ThingsService {


    protected Cache<String, LiveStaleElement<Response>> liveStaleCache;
    private final String RESOURCE_PATH = "http://api.ft.com/things/";
    private final String CACHE_PREFIX = "thing-cache-";

    private final Integer thingByIdTimeOutInMs;

    CacheManager cacheManager;
    private Configuration configuration;
    private LockHandler lockHandler;

    public CachedThingsService(Configuration configuration, Repository repo, ThingsService thingService, Boolean useLocalCache) {
        LiveStaleCache<Response> liveStaleCache = getCache(configuration);

        this.configuration = configuration;
        thingByIdTimeOutInMs = configuration.getThingByIdTimeOutInMs();

        lockHandler = new LockHandler(configuration);
        BlazeQueryService blazeQueryService = new BlazeQueryService(repo, configuration.getDefaultQueryServiceTimeOutInMs() , CACHE_PREFIX, liveStaleCache, lockHandler);


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

    public Thing getThingById(String id) {
        ThingsRequest thingsRequest = new ThingsRequest();
        thingsRequest.id = id;
        thingsRequest.setQueryWaitTimeInMs(this.getThingByIdTimeOutInMs());
        thingsRequest.setResourcePath(RESOURCE_PATH);
        thingsRequest.setLiveCacheTTL(configuration.getLiveCacheTTLInSec());
        return this.cacheManager.retreiveResponse(thingsRequest).getContent(Thing.class);
    }

    @Override
    public Things getThingByLabel(String label, String type) {
        ThingsRequest thingsRequest = new ThingsRequest();
        thingsRequest.id = "NONE";
        thingsRequest.label = label;
        thingsRequest.type = type;
        thingsRequest.setAccept("application/json");
        thingsRequest.setQueryWaitTimeInMs(this.getThingByIdTimeOutInMs());
        thingsRequest.setResourcePath(RESOURCE_PATH);
        thingsRequest.setLiveCacheTTL(configuration.getLiveCacheTTLInSec());
        return this.cacheManager.retreiveResponse(thingsRequest).getContent(Things.class);
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
