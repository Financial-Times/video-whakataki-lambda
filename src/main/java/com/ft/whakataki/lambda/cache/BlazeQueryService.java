package com.ft.whakataki.lambda.cache;


import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;

import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class BlazeQueryService {


    private final String cachePrefix;
    private Repository repository;
    private final int defaultQueryWaitTimeInMs;
    private final ExecutorFactory executorFactory;
    private LiveStaleCache<Response> liveStaleCache;
    private LockHandler lockHandler;

    protected Logger LOG = Logger.getLogger(this.getClass());

    public BlazeQueryService(Repository repository, int defaultQueryWaitTimeInMs, String cachePrefix, LiveStaleCache<Response> liveStaleCache, LockHandler lockHandler) {
        this.repository = repository;
        this.cachePrefix = cachePrefix;
        this.defaultQueryWaitTimeInMs = defaultQueryWaitTimeInMs;
        this.executorFactory = new ExecutorFactory();
        this.liveStaleCache = liveStaleCache;
        this.lockHandler = lockHandler;
    }

    public LookupResult queryAndWait(Request request, String cachePrefix) {
        int queryWaitTimeInMs = this.defaultQueryWaitTimeInMs;
        if (request.queryWaitTimeInMs() != null)
            queryWaitTimeInMs = request.queryWaitTimeInMs();

        LOG.debug("Performing query for " + request.toString() + " will wait " + queryWaitTimeInMs + "ms");

        try {
            long startTime = System.currentTimeMillis();

            ExecutorCompletionService<LookupResult> executorService = new ExecutorCompletionService<LookupResult>(executorFactory.getExecutor());
            Future<LookupResult> future = executorService.submit(new Callable<LookupResult>() {
                @Override
                public LookupResult call() {
                    try {
                        try {
                            Response response = repository.retreiveResponse(request);

                            if (response == null) {
                                LOG.debug("Lookup not found for " + request.toString());
                                liveStaleCache.set(request.getCacheKey(cachePrefix), Response.NOT_FOUND_RESOURCE,
                                        request.getLiveCacheTTL());
                                return LookupResult.notFound();
                            } else {
                                LOG.debug("Lookup found for " + request.toString());
                                liveStaleCache.set(request.getCacheKey(cachePrefix),
                                        response, request.getLiveCacheTTL());
                                return LookupResult.found(response);
                            }
                        } catch (ThingNotFoundException e) {
                            LOG.debug("Lookup not found for " + request.toString());
                            liveStaleCache.set(request.getCacheKey(cachePrefix), Response.NOT_FOUND_RESOURCE,
                                    request.getLiveCacheTTL());
                            return LookupResult.notFound();
                        }
                    } catch (Exception e) {
                        LOG.warn("Lookup failure for " + request.toString(), e);
                        return LookupResult.failure(e);
                    }
                }
            });
            LookupResult result = future.get(queryWaitTimeInMs, TimeUnit.MILLISECONDS);
            long timeTakenToLookupResourceInMs = System.currentTimeMillis() - startTime;
            LOG.info("Resource was available after " + timeTakenToLookupResourceInMs + "ms");
            return result;
        } catch (RejectedExecutionException e) {
            LOG.debug("Lookup rejected by executor for " + request.toString());
            return LookupResult.notReady();
        } catch (TimeoutException e) {
            LOG.debug("Lookup timeout for " + request.toString());
            return LookupResult.notReady();
        } catch (InterruptedException e) {
            LOG.debug("Lookup interrupted for " + request.toString());
            return LookupResult.failure(e);
        } catch (ExecutionException e) {
            LOG.debug("Lookup failed for " + request.toString());
            return LookupResult.failure(e);
        }
    }

}
