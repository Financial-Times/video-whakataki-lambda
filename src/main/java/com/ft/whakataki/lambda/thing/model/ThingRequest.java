package com.ft.whakataki.lambda.thing.model;

import com.ft.whakataki.lambda.common.Request;

import java.io.Serializable;

public class ThingRequest implements Request, Serializable {


    private static final long serialVersionUID = 1409031443738020555L;

    public String thingURI;
    public String environment;
    private String resourcePath;

    private String accept = "application/json";
    private Boolean byPassCache = false;
    private Integer queryWaitTimeInMs = 500;
    private Integer liveCacheTTL = 30;

    @Override
    public String getCacheKey(String cachePrefix) {
        String strId = cachePrefix + this.getResourcePath() +  thingURI + "-Accept:" + accept;
        return String.valueOf(strId.hashCode());
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getThingURI() {
        return thingURI;
    }

    public void setThingURI(String thingURI) {
        this.thingURI = thingURI;
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

    public void setByPassCache(Boolean byPassCache) {
        this.byPassCache = byPassCache;
    }

    public Integer queryWaitTimeInMs() {
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

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }



}
