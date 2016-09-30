package com.ft.whakataki.lambda.cache;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeUtils;

public class LiveStaleElement<V> implements Serializable {

    private static final long serialVersionUID = -5703169966907929405L;

    private static Logger LOG = Logger.getLogger(LiveStaleElement.class);

    private final V data;
    private final long expirationTime;
    private transient final int evictionInSeconds;

    public LiveStaleElement(V data, int evictionInSeconds) {
        this.data = data;
        this.evictionInSeconds = evictionInSeconds;
        this.expirationTime = DateTimeUtils.currentTimeMillis() + (evictionInSeconds * 1000l);
    }

    public boolean isLive() {
        if(LOG.isDebugEnabled()){
            LOG.debug("Checking LiveStale element is live by expirationTime:" + expirationTime + " currentTime:" + DateTimeUtils.currentTimeMillis());
        }
        return expirationTime > DateTimeUtils.currentTimeMillis() ?
                true : false;
    }
    public V getData() {
        return data;
    }

    public int getEvictionInSeconds() {
        return evictionInSeconds;
    }

}
