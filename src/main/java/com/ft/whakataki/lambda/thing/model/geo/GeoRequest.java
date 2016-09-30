package com.ft.whakataki.lambda.thing.model.geo;

import com.ft.whakataki.lambda.common.Request;

import java.io.Serializable;

public class GeoRequest implements Request, Serializable {

    private static final long serialVersionUID = -9098466751317933914L;

    private String resourcePath;
    public String searchString;

    public String environment;
    private String accept = "application/ld+json";

    private Boolean byPassCache = false;
    private Integer queryWaitTimeInMs = 2000;
    private Integer liveCacheTTL = 30;

    @Override
    public String getCacheKey(String cachePrefix) {
        String strId = cachePrefix + this.getResourcePath() + "?searchString=" + this.searchString + "-Accept:" + accept;
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

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public String getAccept() {
        return accept;
    }

    @Override
    public void setAccept(String accept) {
        this.accept = accept;
    }

    @Override
    public Boolean getByPassCache() {
        return this.byPassCache;
    }

    @Override
    public Integer queryWaitTimeInMs() {
         return this.queryWaitTimeInMs;
    }

    public void setQueryWaitTimeInMs(Integer queryWaitTimeInMs) {
        this.queryWaitTimeInMs = queryWaitTimeInMs;
    }

    @Override
    public Integer getLiveCacheTTL() {
        return liveCacheTTL;
    }

    public void setLiveCacheTTL(Integer liveCacheTTL) {
        this.liveCacheTTL = liveCacheTTL;
    }


}
