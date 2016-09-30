package com.ft.whakataki.lambda.thing.model;


import com.ft.whakataki.lambda.common.Request;

import java.io.Serializable;

public class ThingsRequest implements Request, Serializable {


    private static final long serialVersionUID = 9218134176307279480L;

    private String resourcePath;
    public String id;
    public String label;
    public String type;

    public String environment;
    private String accept = "application/json";
    private Boolean byPassCache = false;
    private Integer queryWaitTimeInMs = 350;
    private Integer liveCacheTTL = 30;


    @Override
    public String getCacheKey(String cachePrefix) {
        String strId = cachePrefix + this.getResourcePath() + "?id=" + id + "?label=" + label + "?type=" + type + "-Accept:" + accept;
        return String.valueOf(strId.hashCode());
    }

    @Override
    public String getAccept() {
        return this.accept;
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
        return this.liveCacheTTL;
    }

    public void setLiveCacheTTL(Integer liveCacheTTL) {
        this.liveCacheTTL = liveCacheTTL;
    }

    public void setByPassCache(Boolean byPassCache) {
        this.byPassCache = byPassCache;
    }


    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }


    public String toString() {

        return "resourcepath: " + resourcePath + " id: " + id;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQueryWaitTimeInMs() {
        return queryWaitTimeInMs;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
