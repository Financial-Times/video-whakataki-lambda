package com.ft.whakataki.lambda.cache;


import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import org.apache.log4j.Logger;

public class CacheManager implements Repository {

    protected Logger LOG = Logger.getLogger(this.getClass());

    private BlazeQueryService blazeQueryService;

    private LiveStaleCache<Response> liveStaleCache;

    private LockHandler lockHandler;

    private String cachePrefix;

    public CacheManager(BlazeQueryService blazeQueryService, LiveStaleCache<Response> liveStaleCache, String cachePrefix, LockHandler lockHandler) {
        this.blazeQueryService = blazeQueryService;
        this.liveStaleCache = liveStaleCache;
        this.cachePrefix = cachePrefix;
        this.lockHandler = lockHandler;
    }


    public Response retreiveResponse(Request request) {
        if (request.getByPassCache()) {
            return performQuery(request, this.getCachePrefix());

        } else {
            return loadResponseUsingCaches(request);
        }
    }

    private Response loadResponseUsingCaches(Request request) {
        Response response = liveStaleCache.get(request.getCacheKey(cachePrefix));
        if (response != null) {
            LOG.debug("Resource obtained from live cache");
            return response;
        }

        String lockKey = request.getCacheKey(cachePrefix) + "-lock*";
        if (!lockHandler.obtainLock(lockKey)) {
            Response staleResponse = liveStaleCache.getStale(request.getCacheKey(cachePrefix));
            if (staleResponse == null) {
                LOG.debug("Resource locked");
                throw new ThingLambdaException("Resource locked whilst another request performs a lookup");
            }
            LOG.debug("Resource from stale because resource locked");
            return staleResponse;
        }

        try {
            return performQuery(request, cachePrefix);
        }
        finally {
            lockHandler.releaseLock(lockKey);
        }
        // Destroy

    }


    private Response performQuery(Request request, String cachePrefix) {
        LookupResult queryLookUpResult = this.getBlazeQueryService().queryAndWait(request, cachePrefix);
        if (queryLookUpResult.isReady()) {
            if (queryLookUpResult.isFailure()) {
                Response staleResponse = this.getLiveStaleCache().getStale(request.getCacheKey(cachePrefix));
                if (staleResponse == null) {
                    LOG.debug("Resource loading failure");
                    // Change this to Server Error
                    throw new ThingNotFoundException("500 - Query Failure and stale cache miss [" + request.getCacheKey(this.cachePrefix) + "]");
                }
                LOG.debug("Resource from stale because of failure to load", queryLookUpResult.getCause());
                return staleResponse;
            }
            if (queryLookUpResult.isFound()) {
                LOG.debug("Resource found");
                return queryLookUpResult.getResponse();
            }
            LOG.debug("Resource not found");
            throw new ThingNotFoundException("404 - Thing Not Found [" + request.getCacheKey(this.cachePrefix) + "]");

        } else {
            Response staleResponse= liveStaleCache.getStale(request.getCacheKey(cachePrefix));
            if (staleResponse == null) {
                LOG.debug("Resource not loaded in time");
                throw new ThingLambdaException("Could not obtain the resource in time");
            }
            LOG.debug("Resource from stale because not loaded in time");
            return staleResponse;
        }
    }


    public BlazeQueryService getBlazeQueryService() {
        return blazeQueryService;
    }

    public void setBlazeQueryService(BlazeQueryService blazeQueryService) {
        this.blazeQueryService = blazeQueryService;
    }

    public LiveStaleCache<Response> getLiveStaleCache() {
        return liveStaleCache;
    }

    public void setLiveStaleCache(LiveStaleCache<Response> liveStaleCache) {
        this.liveStaleCache = liveStaleCache;
    }

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public void destroyCacheConnection() {
        this.liveStaleCache.destroy();
    }
}
