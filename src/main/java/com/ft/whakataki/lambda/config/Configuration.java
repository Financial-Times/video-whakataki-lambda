package com.ft.whakataki.lambda.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;

public class Configuration {

    @JsonProperty @NotNull
    private String serviceURL;

    @JsonProperty @NotNull
    private String namespace;

    @JsonProperty @NotNull
    private  Boolean useLBS = false;

    @JsonProperty @NotNull
    private String apiGw;

    @JsonProperty @NotNull
    private Boolean useLocalCache;

    @JsonProperty
    private Integer defaultQueryServiceTimeOutInMs;

    @JsonProperty @NotNull
    private Integer thingByIdTimeOutInMs;

    @JsonProperty @NotNull
    private Integer liveCacheTTLInSec;

    @JsonProperty @NotNull
    private Integer staleCacheTTLInSec;

    @JsonProperty @NotNull
    private String memcacheServers;

    @JsonProperty @NotNull
    private String memcacheProtocal;

    @JsonProperty @NotNull
    private Integer memcacheOpTimeout;

    @JsonProperty @NotNull
    private Integer memcacheTimeoutExceptionThreshold;

    @JsonProperty @NotNull
    private Integer memcacheModificationTimeoutInSec;

    @JsonProperty @NotNull
    private Integer memcacheAccessTimeoutInSec;

    @JsonProperty @NotNull
    private Boolean useCache;

    @JsonProperty @NotNull
    private Integer geoByLabelTimeOutInMs;

    @JsonProperty @NotNull
    private Integer geoByLiveCacheTTLSec;

    public Boolean getUseLBS() {
        return useLBS;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getApiGw() {
        return apiGw;
    }

    public void setApiGw(String apiGw) {
        this.apiGw = apiGw;
    }

    public Boolean getUseLocalCache() {
        return useLocalCache;
    }

    public void setUseLocalCache(Boolean useLocalCache) {
        this.useLocalCache = useLocalCache;
    }


    public Integer getThingByIdTimeOutInMs() {
        return thingByIdTimeOutInMs;
    }

    public void setThingByIdTimeOutInMs(Integer thingByIdTimeOutInMs) {
        this.thingByIdTimeOutInMs = thingByIdTimeOutInMs;
    }

    public Integer getDefaultQueryServiceTimeOutInMs() {
        return defaultQueryServiceTimeOutInMs;
    }

    public void setDefaultQueryServiceTimeOutInMs(Integer defaultQueryServiceTimeOutInMs) {
        this.defaultQueryServiceTimeOutInMs = defaultQueryServiceTimeOutInMs;
    }

    public Integer getLiveCacheTTLInSec() {
        return liveCacheTTLInSec;
    }

    public void setLiveCacheTTLInSec(Integer liveCacheTTLInSec) {
        this.liveCacheTTLInSec = liveCacheTTLInSec;
    }

    public void setUseLBS(Boolean useLBS) {
        this.useLBS = useLBS;
    }

    public Integer getStaleCacheTTLInSec() {
        return staleCacheTTLInSec;
    }

    public void setStaleCacheTTLInSec(Integer staleCacheTTLInSec) {
        this.staleCacheTTLInSec = staleCacheTTLInSec;
    }

    public String getMemcacheServers() {
        return memcacheServers;
    }

    public void setMemcacheServers(String memcacheServers) {
        this.memcacheServers = memcacheServers;
    }

    public String getMemcacheProtocal() {
        return memcacheProtocal;
    }

    public void setMemcacheProtocal(String memcacheProtocal) {
        this.memcacheProtocal = memcacheProtocal;
    }

    public Integer getMemcacheOpTimeout() {
        return memcacheOpTimeout;
    }

    public void setMemcacheOpTimeout(Integer memcacheOpTimeout) {
        this.memcacheOpTimeout = memcacheOpTimeout;
    }

    public Integer getMemcacheTimeoutExceptionThreshold() {
        return memcacheTimeoutExceptionThreshold;
    }

    public void setMemcacheTimeoutExceptionThreshold(Integer memcacheTimeoutExceptionThreshold) {
        this.memcacheTimeoutExceptionThreshold = memcacheTimeoutExceptionThreshold;
    }

    public Integer getMemcacheModificationTimeoutInSec() {
        return memcacheModificationTimeoutInSec;
    }

    public void setMemcacheModificationTimeoutInSec(Integer memcacheModificationTimeoutInSec) {
        this.memcacheModificationTimeoutInSec = memcacheModificationTimeoutInSec;
    }

    public Integer getMemcacheAccessTimeoutInSec() {
        return memcacheAccessTimeoutInSec;
    }

    public void setMemcacheAccessTimeoutInSec(Integer memcacheAccessTimeoutInSec) {
        this.memcacheAccessTimeoutInSec = memcacheAccessTimeoutInSec;
    }

    public Boolean getUseCache() {
        return useCache;
    }

    public void setUseCache(Boolean useCache) {
        this.useCache = useCache;
    }

    public Integer getGeoByLabelTimeOutInMs() {
        return geoByLabelTimeOutInMs;
    }

    public void setGeoByLabelTimeOutInMs(Integer geoByLabelTimeOutInMs) {
        this.geoByLabelTimeOutInMs = geoByLabelTimeOutInMs;
    }

    public Integer getGeoByLiveCacheTTLSec() {
        return geoByLiveCacheTTLSec;
    }

    public void setGeoByLiveCacheTTLSec(Integer geoByLiveCacheTTLSec) {
        this.geoByLiveCacheTTLSec = geoByLiveCacheTTLSec;
    }
}
