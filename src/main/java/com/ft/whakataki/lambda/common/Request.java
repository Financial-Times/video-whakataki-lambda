package com.ft.whakataki.lambda.common;


public interface Request {

    public String   getCacheKey(String cachePrefix);

    public String getAccept();

    public void setAccept(String accept);

    public Boolean getByPassCache();

    public Integer queryWaitTimeInMs();

    public Integer getLiveCacheTTL();

}
