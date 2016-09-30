package com.ft.whakataki.lambda.cache.spy;

public class MemcachedClientHolder {
    private Object memcachedClientObj;
    private final boolean hasClient;

    public MemcachedClientHolder() {
        this.hasClient = false;
    }

    public MemcachedClientHolder(Object client) {
        this(client,(client == null) ? false: true);
    }

    public MemcachedClientHolder(Object client,boolean hasClient) {
        this.hasClient = hasClient;
        memcachedClientObj=client;
    }

    /**
     * Returns if a concreate (an actual memcached client implementation that talks to memcached)
     * client object is available.
     */
    public boolean hasClient() {
        return hasClient;
    }

    /**
     * Returns the client specific implementation of the memcached connection
     * used to talk to memcached.  If {@link #hasClient()} returns false; then you should not use
     * the object returned by this to talk to memcached; there is no gaurantee that the
     * object is either not null, or an implementation of the client library.  if
     * {@link #hasClient()} is true, then this will return a client library implementation
     * @return
     */
    public Object getMemcachedClientObj() {
        return memcachedClientObj;
    }


}
