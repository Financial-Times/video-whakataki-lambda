package com.ft.whakataki.lambda.thing.model.geo;

import com.ft.whakataki.lambda.common.Request;

import java.io.Serializable;

public class GeoIdRequest implements Serializable, Request {

    private static final long serialVersionUID = -7654922452284222347L;

    private String resourcePath;
    public String id;

    public String environment;
    private String accept = "application/ld+json";

    private Boolean byPassCache = false;
    private Integer queryWaitTimeInMs = 2000;
    private Integer liveCacheTTL = 30;

    @Override
    public String getCacheKey(String cachePrefix) {
        String strId = cachePrefix + this.getResourcePath() + this.id + "-Accept:" + accept;
        return String.valueOf(strId.hashCode());
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }



    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public Boolean getByPassCache() {
        return byPassCache;
    }

    @Override
    public Integer queryWaitTimeInMs() {
        return null;
    }

    public void setByPassCache(Boolean byPassCache) {
        this.byPassCache = byPassCache;
    }

    public Integer getQueryWaitTimeInMs() {
        return queryWaitTimeInMs;
    }

    public void setQueryWaitTimeInMs(Integer queryWaitTimeInMs) {
        this.queryWaitTimeInMs = queryWaitTimeInMs;
    }

    public Integer getLiveCacheTTL() {
        return liveCacheTTL;
    }

    public void setLiveCacheTTL(Integer liveCacheTTL) {
        this.liveCacheTTL = liveCacheTTL;
    }
}
