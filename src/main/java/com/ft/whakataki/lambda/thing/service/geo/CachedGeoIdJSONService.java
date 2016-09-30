package com.ft.whakataki.lambda.thing.service.geo;

import com.ft.whakataki.lambda.cache.*;
import com.ft.whakataki.lambda.cache.spy.Cache;
import com.ft.whakataki.lambda.cache.spy.LocalCache;
import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.thing.model.geo.GNLocation;
import com.ft.whakataki.lambda.thing.model.geo.GeoIdRequest;

public class CachedGeoIdJSONService implements GeoIdJSONService {

    protected Cache<String, LiveStaleElement<Response>> liveStaleCache;
    private final String RESOURCE_PATH = "http://api.ft.com/things/geo/";
    private final String CACHE_PREFIX = "geo-cache-";

    private GeoIdJSONService geoIdJSONService;

    private final Integer geoByLabelTimeOutInMs;
    private final Integer geoByLiveCacheTTLSec;

    CacheManager cacheManager;
    private Configuration configuration;
    private LockHandler lockHandler;

    public CachedGeoIdJSONService(Configuration configuration, Repository repo, GeoIdJSONService geoIdJSONService, Boolean useLocalCache) {
        LiveStaleCache<Response> liveStaleCache = getCache(configuration);

        this.configuration = configuration;

        this.geoByLabelTimeOutInMs = configuration.getGeoByLabelTimeOutInMs();
        this.geoByLiveCacheTTLSec = configuration.getLiveCacheTTLInSec();

        lockHandler = new LockHandler(configuration);
        BlazeQueryService blazeQueryService = new BlazeQueryService(repo, configuration.getDefaultQueryServiceTimeOutInMs() , CACHE_PREFIX, liveStaleCache, lockHandler);

        cacheManager = new CacheManager(blazeQueryService, liveStaleCache, CACHE_PREFIX, lockHandler);

        this.geoIdJSONService = geoIdJSONService;
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

    @Override
    public GNLocation getGeoById(String id) {
        GeoIdRequest geoIdRequest = new GeoIdRequest();

        geoIdRequest.setAccept("application/json");
        geoIdRequest.setQueryWaitTimeInMs(this.geoByLabelTimeOutInMs);
        geoIdRequest.setResourcePath(RESOURCE_PATH);
        geoIdRequest.setLiveCacheTTL(this.geoByLiveCacheTTLSec);
        geoIdRequest.setId(id);
        return this.cacheManager.retreiveResponse(geoIdRequest).getContent(GNLocation.class);
    }
}
