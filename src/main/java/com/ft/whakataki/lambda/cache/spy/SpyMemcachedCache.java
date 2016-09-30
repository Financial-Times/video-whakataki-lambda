package com.ft.whakataki.lambda.cache.spy;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.OperationTimeoutException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class SpyMemcachedCache implements ExpiringCache<String,Serializable> {

    private UUID id;

    transient private static final Logger logger = Logger.getLogger(SpyMemcachedCache.class);


    /**
     * <p>
     * cannot store anything in memcache for longer than 3 days You should
     * really set the default eviction timeout to a different value.
     * </p>
     *
     * @see #setEvictionTimeout(int)
     */
    public static final int DEFAULT_EVICTION_SECONDS = 180;
    /**
     * default amount of time to wait in seconds for async methods
     */
    public static final int DEFAULT_ACCESS_TIMEOUT_IN_SECONDS = 1;
    public static final int NO_SET_TIMEOUT = -1;
    public static final int DEFAULT_SET_TIMEOUT_IN_SECONDS = 1000;
    public static final int DEFAULT_SET_TIMEOUT_IN_MILLIS = 1000;
    public static final long DEFAULT_ACCESS_TIMEOUT_IN_MILLIS = 500; //DEFAULT_ACCESS_TIMEOUT_IN_SECONDS * 1000;
    /**
     * default amount of time to wait in millis for set operations, that wait
     * for the result of values.
     */
    public static final long DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS = 500;
    public static final long NO_REMOVAL_TIMEOUT=-1;
    /**
     * default amount of time to wait in millis for delete operation to complete;
     * after that amount of time the delete request is cancelled.
     * Default is -1 to be backwards compatible with old library; which did
     * not have a timeout for removal operations
     */
    public static final long DEFAULT_REMOVAL_TIMEOUT_IN_MILLIS = NO_REMOVAL_TIMEOUT;

    /**
     * The default timeout for evictions.
     */
    private int evictionTimeout = DEFAULT_EVICTION_SECONDS;

    transient private MemcachedClientIF memcachedClient;
    public boolean initialisedWithMock = false;


    private boolean failSilently = false;

    /**
     * How long should we wait for async processes in seconds?
     */
    private int accessTimeout = DEFAULT_ACCESS_TIMEOUT_IN_SECONDS;

    /**
     * How long should we wait for async processes in milliseconds?
     */
    private long accessTimeoutInMillis = DEFAULT_ACCESS_TIMEOUT_IN_MILLIS;

    /**
     * Amount of time to wait in millis for modification operations (add,
     * replace) that wait for the result of values.
     */
    private long modificationTimeoutInMillis = DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS;

    /**
     * Amount of time to wait in millis for delete operations to complete.
     * -1 means do not wait (for backwards compatibility)
     */
    private long removalTimeoutInMillis = DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS;

    /**
     * default amount of time to wait in millis for set operations, rather than
     * just returning.
     */
    private long setTimeoutInMillis = DEFAULT_SET_TIMEOUT_IN_MILLIS;

    SpyMemcachedCache() {
        logger.fatal("SpyMemcached Client UUID: " + toString());
    }



    /**
     * Create a MemcachedCache object with default eviction and access timeouts
     *
     * <ul>
     * <li>Access timeout value : {@value #DEFAULT_ACCESS_TIMEOUT_IN_MILLIS}</li>
     * <li>Eviction timeout value : {@value #DEFAULT_EVICTION_SECONDS}</li>
     * </ul>
     *
     * @param memcachedClient
     *            The spy memcached client (MemcachedClientIF) or a MemcachedClientHolder
     */
    public SpyMemcachedCache(Object memcachedClient) {
        this(memcachedClient, DEFAULT_ACCESS_TIMEOUT_IN_SECONDS, DEFAULT_EVICTION_SECONDS);
    }

    /**
     * Creates the spy memcached object, taking in the spy memcached connection
     * interface and, a default timeout for how long we should wait for
     * asynchronous requests. The asynchronous method are:
     * <ul>
     * <li>{@link #get(String)}</li>
     * </ul>
     *
     *
     * @param memcachedClient
     *            The memcached connection.
     * @param accessTimeout
     *            Default timeout of asynchronous requests, in seconds.
     */
    public SpyMemcachedCache(Object memcachedClient, final int accessTimeout) {
        this(memcachedClient, accessTimeout, DEFAULT_EVICTION_SECONDS);
    }


    /**
     * Creates the spy memcached object, taking in the spy memcached connection
     * interface and, a default timeout for how long we should wait for
     * asynchronous requests. The asynchronous method are:
     * <ul>
     * <li>{@link #get(String)}</li>
     * </ul>
     * This constructor alos takes in a parameters that sets the default timeout
     * for the expiry of items stored in the cache (in seconds), and the default
     * timeout, in seconds, for asynchronous requests
     *
     *
     * @param memcachedClientObj
     *            The memcached connection.
     * @param accessTimeout
     *            The timeout of asynchronous requests, in seconds
     * @param evictionTimeout
     *            The eviction time for entries added/replaced and set on the
     *            cache

     */
    public SpyMemcachedCache(Object memcachedClientObj,
                             final int accessTimeout,
                             final int evictionTimeout) {

        MemcachedClientHolder memcachedClient = null;
        if(memcachedClientObj instanceof MemcachedClientIF) {
            memcachedClient = new MemcachedClientHolder((MemcachedClientIF)memcachedClientObj);
        } else if(memcachedClientObj instanceof MemcachedClientHolder) {
            memcachedClient = (MemcachedClientHolder)memcachedClientObj;
        } else {
            throw new InstantiationError("memcached object at constructor index 0 must be a MemcachedClientIF or a MemcachedClientHolder object");
        }

        id = UUID.randomUUID();


        logger.info("SpyMemcached Client UUID: " + toString());

        if(memcachedClient.hasClient()) {
            Object mObj = memcachedClient.getMemcachedClientObj();

            this.memcachedClient = (MemcachedClientIF) memcachedClient.getMemcachedClientObj();

        }
        setAccessTimeout(accessTimeout);
        setEvictionTimeout(evictionTimeout);
    }


    /**
     * <p>
     * Setting the name for the cache object that this cache cache interface represents.
     * This should only be set once, at construction time.  This can be used to identify the client
     * memcached application, and is useful for the Memcached Replication Service; so replication stats
     * can be generated for the particular client.  If this isn't set then the default of PROFILE is used,
     * which is the name of the tomcat, and provided at startup (of the tomcat) in the System Property: PROFILE
     * </p>
     * <p>
     * Please note that as a side affect of running this method it will output to your logs, as a fatal message
     * (just so it is output) the id of this client.
     * </p>
     * @param name The name of the application
     */
    public void setName(String name) {

        id = UUID.randomUUID();

    }

    /**
     * Returns the name of the cache object.  This is used to identify the cache object and can
     * be to used to differentiate the client from any other memcached clients in the jvm, in order
     * to produce stats based on the number of hits etc, being produced by this particular client.
     *
     *
     * @return the name of the cache object
     */
    public String getName()
    {
        return id.toString();
    }

    /**
     * Obtains an object that was deserialized from memcached; that has been
     * stored under the given key value.
     *
     * @param key
     *            The key under which to find an object in memcached.
     * @return The java object that was stored in memcache. Null if object did
     *         not exist
     */
    public Serializable get(String key) {
        if (key == null) {
            if (logger.isInfoEnabled()) {
                logger.warn("key is null", new Exception());
            }
            return null;
        }

        Future<Object> future = null;
        try {
            future = memcachedClient.asyncGet(key);
        } catch (IllegalArgumentException e) {
            throw e;
        }

        try {
            if (future == null)
                return null;

            return (Serializable) future.get(getAccessTimeoutInMillis(), TimeUnit.MILLISECONDS);

        } catch (TimeoutException e) {

            future.cancel(false);
            logger.error("memcached timed out for expression " + key + " " + e.getMessage());
        } catch (Exception e) {
            logger.error("memcached interuppted for expression " + key + " " + e.getMessage());
        }


        return null;

    }

    /**
     * Bulk obtains an objects that was deserialized from memcached; that has
     * been stored under the given list of key values.
     *
     * @param list
     *            the list of keys to look for in memcached.
     * @return
     *            Map of the results, a value per key. The map will be empty if
     *            not keys exists in memcached.
     */
    public Map<String, Serializable> get(List<String> list) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (list.size() == 0)
            return new HashMap<String, Serializable>();

        try {
            result = memcachedClient.getBulk(list);
        } catch (OperationTimeoutException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new OperationTimeoutException(e.getMessage(), e);
        }

        Map<String, Serializable> returnResult = new HashMap<String, Serializable>();

        if (result != null) {
            for (Map.Entry<String, Object> entry : result.entrySet())
                returnResult.put(entry.getKey(), (Serializable) result.get(entry.getKey()));
        }

        return returnResult;
    }


    /**
     * <p>
     * Returns whether the delete operation succeeded.  The operation should only be used if
     * {@link #setRemovalTimeoutInMillis(long)} has be set to positive a value,
     * not left as default (which is no timeout {@value #NO_REMOVAL_TIMEOUT}),
     * and isn't equal to {@value #NO_REMOVAL_TIMEOUT}.
     * </p>
     * <p>
     * Otherwise all that will be returned is a success to say the operation has been
     * submitted to execution; not whether it is successful or not.
     * </p>
     * @param key
     *            The key to remove from memcached
     *
     *
     * @return boolean if the delete was successful or not
     */
    public boolean deleteOperation(String key)
    {
        return (Boolean)delete(key,false);
    }

    /**
     * Deletes an object from memcached that is stored under the given key.
     *
     * @param key The key to remove from memcached
     * @param returnCache
     *            Whether we wish to know if the set operation completed (false - and returns a boolean)
     *            or whether just returns itself.
     *
     * @return this object, itself, or a boolean representing if delete operation was successful
     */
    private Object delete(String key, boolean returnCache) {
        if (key == null)
        {
            return (returnCache) ? this : false;
        }

        long timeout = getRemovalTimeoutInMillis();

        Future<Boolean> result = null;
        try {
            result = memcachedClient.delete(key);


        } catch (IllegalArgumentException e) {
            // if there's an illegal argument, we should cancel the op and return.
            try
            {
                if(result!=null) result.cancel(true);
            }
            catch(Exception e2) {
                if(logger.isEnabledFor(Level.ERROR)) logger.error("Error cancelling operation, due to illegal argument in set",e2);
            }


            throw e;
        }

        boolean success = false;

        try {
            if (result == null)
                return (returnCache) ? this : false;

            success = result.get(timeout, TimeUnit.MILLISECONDS).booleanValue();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
        } catch (CancellationException e) {

        } catch (TimeoutException e) {
            result.cancel(false);
            if(logger.isEnabledFor(Level.ERROR))
                logger.error("memcached timed out for expression " + key + " " + e.getMessage());
        }

        return (returnCache) ? this : success;
    }

    /**
     * Deletes an object from memcached that is stored under the given key.
     *
     * @return this object, itself
     */
    public Cache<String, Serializable> delete(String key) {
        return (Cache<String, Serializable>)delete(key,true);
    }

    /**
     * Deletes a list of object from memcached that is stored under the given
     * list of keys. This is not a bulk delete operation. Many requests are send
     * to the server one after the other to delete object.
     *
     * @param keys
     *            the list of keys to delete objects for.
     * @return this object, itself.
     */
    public Cache<String, Serializable> delete(List<String> keys) {
        for (String key : keys) {
            if (key == null)
                continue;

            delete(key);
        }

        return this;
    }

    /**
     * Inserts a value into memcached regardless of any existing value. The item
     * is given the default expiry time. {@link #getEvictionTimeout()}
     */
    public Cache<String, Serializable> set(String key, Serializable value) {

        return set(key, getEvictionTimeout(), value);

    }

    /**
     * <p>
     * Returns whether the set operation succeeded.  The operation should only be used if
     * {@link #setSetTimeoutInMillis(long)} has be set to positive a value, not left as default (which is
     * no timeout {@value #NO_SET_TIMEOUT}), and isn't equal to {@value #NO_SET_TIMEOUT}.
     * </p>
     * <p>
     * Otherwise all that will be returned is a success to say the operation has been
     * submitted to execution; not whether it is successful or not.
     * </p>
     * @param key
     *            The key under which to store an object in memcached
     * @param evictionTimeoutSeconds
     *            The amount of seconds that item should live in memcached
     * @param value
     *            The object that is to be serialized into memcached.
     *
     * @return boolean if the set was successful or not
     */
    public boolean setOperation(String key, int evictionTimeoutSeconds, Serializable value)
    {
        return (Boolean)set(key, evictionTimeoutSeconds, value, false);
    }


    /**
     * Inserts a value into memcached regardless of any existing value.
     *
     * @param key
     *            The key under which to store an object in memcached
     * @param evictionTimeoutSeconds
     *            The amount of seconds that item should live in memcached
     * @param value
     *            The object that is to be serialized into memcached.
     * @param returnCache
     *            Whether we wish to know if the set operation completed (false - and returns a boolean)
     *            or whether just returns itself.
     *
     * @return this object, itself
     */
    private Object set(String key, int evictionTimeoutSeconds, Serializable value, boolean returnCache)
    {
        if (key == null || value == null)
        {
            return (returnCache) ? this : false;
        }

        long timeout = getSetTimeoutInMillis();

        Future<Boolean> b = null;
        try {
            b = memcachedClient.set(key, evictionTimeoutSeconds, value);

            // if not time out, replicate the set.
            if (b != null && timeout == NO_SET_TIMEOUT)
            {


                // can only return true for a async operation, as we don't know the result of the operation
                return (returnCache) ? this : true;
            }

        } catch (IllegalArgumentException e) {
            // if there's an illegal argument, we should cancel the op and return.
            try
            {
                if(b!=null) b.cancel(true);
            }
            catch(Exception e2) {
                if(logger.isEnabledFor(Level.ERROR)) logger.error("Error cancelling operation, due to illegal argument in set",e2);
            }


            throw e;

        }

        boolean success = false;
        if (b != null) {
            try {
                success = b.get(timeout, TimeUnit.MILLISECONDS).booleanValue();

            } catch (InterruptedException e) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("memcached set expression interrupted " + key + " " + e.getMessage());
                }
            } catch (ExecutionException e) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("memcached set expression execution failed " + key + " " + e.getMessage());
                }
            } catch (TimeoutException e) {
                b.cancel(true);
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("memcached timed out for set expression " + key + " " + e.getMessage());
                }
            }
        }

        return (returnCache) ? this : success;
    }

    /**
     * Inserts a value into memcached regardless of any existing value.
     *
     * @param key
     *            The key under which to store an object in memcached
     * @param evictionTimeoutSeconds
     *            The amount of seconds that item should live in memcached
     * @param value
     *            The object that is to be serialized into memcached.
     *
     * @return this object, itself
     */
    public Cache<String, Serializable> set(String key, int evictionTimeoutSeconds, Serializable value) {
        return (Cache<String, Serializable>)set(key, evictionTimeoutSeconds, value, true);
    }

    /**
     * <p>
     * Inserts multiple values into memcached regardless of any existing value.
     * The values to set are given as a map, and are to be stored in memcached
     * under the corresponding key; as provided in the give map.
     * </p>
     * <p>
     * This is not a bulk insert, and a separate attempt is made to insert each
     * value into memcached.
     * </p>
     * <p>
     * Which key is set first in memcached is determined by the map
     * implementation, and its ordering semantics, and not determined by this
     * method.
     * </p>
     *
     * @param entries
     *            The map of entries to set/insert into memcached
     * @return this object itself
     *
     */
    public Cache<String, Serializable> set(Map<String, Serializable> entries) {

        for (Map.Entry<String, Serializable> entry : entries.entrySet())
            set(entry.getKey(), getEvictionTimeout(), (Serializable) entry.getValue());

        return this;

    }

    /**
     * Shutdown the persistent connection to memcached
     *
     * @return
     */
    public Object destroy() {
        memcachedClient.shutdown();
        return this;
    }

    /**
     * You cannot flush all items from the cache, if this method was implemented
     * you could basically flush the entire cache
     *
     * @throws UnsupportedOperationException
     */
    public Cache<String, Serializable> flushAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * <p>
     * Adds multiple entries in the cache. This is not a bulk operation, it
     * performs multiple add operations, one after the other over the wire to
     * the memcached server.
     * </p>
     * <p>
     * The entries are passed as a map of key and values. The values are stored
     * under the given keys in memcached. Only stores an entry in the cache if
     * it doesn't exist already
     * </p>
     * <p>

     * </p>
     * <p>
     * This method will only return true if all adds, for all entries succeeded
     * </p>
     *
     * @param entries
     *            the map of objects to set on the
     */
    public boolean add(Map<String, Serializable> entries) {
        if (entries == null || entries.size() == 0)
            return true;



        boolean allAdded = false;

        for (Map.Entry<String, Serializable> entry : entries.entrySet()) {
            if (add(entry.getKey(), (Serializable) entries.get(entry.getKey()))) {
                allAdded = true;
            } else {
                allAdded = false;
            }
        }
        return allAdded;
    }


    /**
     * <p>
     * Stores an entry in memcached only if it doesn't exist already. The given
     * object is store under the give key value.
     * </p>
     * <p>
     * This method will wait for the result of the add operation, and return the
     * result. Please use {@link #setModificationTimeoutInMillis(long)} to set
     * the timeout for this operation, the default is
     * {@value #DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS}
     * </p>
     *
     * @param key
     *            The key under which to add a value
     * @param val
     *            The java object (value) to serialize to memcached
     */
    public boolean add(String key, Serializable val)
    {
        return add(key,getEvictionTimeout(), val);
    }

    /**
     * @see #add(String, int, Serializable)
     */
    public boolean addOperation(String key, int evictionTimeoutSeconds, Serializable val) {
        return add(key,evictionTimeoutSeconds,val);
    }

    /**
     * <p>
     * Stores an entry in memcached only if it doesn't exist already. The given
     * object is store under the give key value.
     * </p>
     * <p>
     * This method will wait for the result of the add operation, and return the
     * result. Please use {@link #setModificationTimeoutInMillis(long)} to set
     * the timeout for this operation, the default is
     * {@value #DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS}
     * </p>
     *
     * @param key
     *            The key under which to add a value
     *
     * @param evictionTimeoutSeconds
     *            The time after which the cached item will be expired from memcached
     *
     * @param val
     *            The java object (value) to serialize to memcached
     *
     *
     */
    public boolean add(String key, int evictionTimeoutSeconds, Serializable val) {
        if (key == null || val == null)
            return false;


        if (logger.isDebugEnabled())
            logger.debug("Adding key to memcached " + key);

        Future<Boolean> result = null;

        try {
            result = memcachedClient.add(key, evictionTimeoutSeconds, val);


        } catch (IllegalArgumentException e) {
            throw e;
        }

        try {

            if (result == null)
                return false;

            return result.get(getModificationTimeoutInMillis(), TimeUnit.MILLISECONDS).booleanValue();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
        } catch (CancellationException e) {

        } catch (TimeoutException e) {
            result.cancel(false);
            logger.error("memcached timed out for expression " + key + " " + e.getMessage());
        }

        return false;
    }

    /**
     * <p>
     * Replaces the values for multiple entries in the cache. This is not a bulk
     * operation, it performs multiple replace operations, one after the other
     * over the wire to the memcached server.
     * </p>
     * <p>
     * The entries are passed as a map of key and values. The values are stored
     * under the given keys in memcached. Only stores an entry in the cache if
     * it already exists
     * </p>
     * <p>
     * This method will only return true if all replaces, for all entries
     * succeeded
     * </p>
     *
     * @param entries
     *            The map of objects to set on the memcached server
     */
    public boolean replace(Map<String, Serializable> entries) {

        if (entries == null || entries.size() == 0)
            return true;
        boolean allReplaced = false;


        // TODO this will only return the result of the last entry in the map
        for (Map.Entry<String, Serializable> entry : entries.entrySet()) {
            if (replace(entry.getKey(), (Serializable) entries.get(entry.getKey()))) {
                allReplaced = true;
            } else {
                allReplaced = false;
            }
        }

        return allReplaced;
    }

    /**
     * <p>
     * Replace an object with the given value if there is already a value for
     * the given key. If the key isn't in memcached then the insert will not be
     * performed.
     * </p>
     * <p>
     * The given key's value will be replaced with the given Java object, which
     * is serialized to memcached to be persisted.
     * </p>
     * <p>
     * This method will wait for the result of the add operation, and return the
     * result. Please use {@link #setModificationTimeoutInMillis(long)} to set
     * the timeout for this operation, the default is
     * {@value #DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS}
     * </p>
     *
     * @param key, whose value to replace in memached
     * @param val to setfor the given key if it exists
     *
     * @return Did the replace succeed.
     */
    public boolean replace(String key, Serializable val) {
        return replace(key,getEvictionTimeout(),val);
    }

    /**
     * @see #replace(String, int, Serializable)
     */
    public boolean replaceOperation(String key,int evictionTimeoutSeconds, Serializable val) {
        return replace(key,evictionTimeoutSeconds,val);
    }


    /**
     * <p>
     * Replace an object with the given value if there is already a value for
     * the given key. If the key isn't in memcached then the insert will not be
     * performed.
     * </p>
     * <p>
     * The given key's value will be replaced with the given Java object, which
     * is serialized to memcached to be persisted.
     * </p>
     * <p>
     * This method will wait for the result of the add operation, and return the
     * result. Please use {@link #setModificationTimeoutInMillis(long)} to set
     * the timeout for this operation, the default is
     * {@value #DEFAULT_MODIFICATION_TIMEOUT_IN_MILLIS}
     * </p>
     *
     * @param key, whose value to replace in memached
     *
     * @param evictionTimeoutSeconds
     *            The time after which the cached item will be expired from memcached
     * @param val to setfor the given key if it exists
     *
     * @return Did the replace succeed.
     */
    public boolean replace(String key,int evictionTimeoutSeconds, Serializable val) {
        if (key == null || val == null)
            return false;

        Future<Boolean> result = null;

        try {
            result = memcachedClient.replace(key, evictionTimeoutSeconds, val);

        } catch (IllegalArgumentException e) {
            throw e;
        }

        try {
            if (result == null)
                return false;

            return result.get(getModificationTimeoutInMillis(), TimeUnit.MILLISECONDS).booleanValue();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
        } catch (CancellationException e) {

        } catch (TimeoutException e) {
            result.cancel(false);
            logger.error("memcached timed out for expression " + key + " " + e.getMessage());
        }

        return false;
    }

    /**
     * Returns the default expiry time, in seconds, that a key will be insert
     * into memcached with; if an expiry time is not specified
     *
     * @return the time in seconds
     */
    public int getEvictionTimeout() {
        return this.evictionTimeout;
    }

    /**
     * Sets the default expiry time that an item will be inserted into
     * memcached, when an item is set in the cache.
     *
     * @param evictionTimeout
     *            the default expiry time for objects in the cache, in seconds
     */
    public void setEvictionTimeout(int evictionTimeout) {
        this.evictionTimeout = evictionTimeout;
    }

    public MemcachedClientIF getMemcachedClient() {
        return memcachedClient;
    }


    /**
     * Returns the amount of time that is waited for on async methods, in millis
     *
     * @return Returns the amount of time in millis to wait for async methods
     */
    public long getAccessTimeoutInMillis() {
        return accessTimeoutInMillis;
    }

    /**
     * Set the amount of time that is waited for on async methods, in millis
     *
     * @param timeInMillis
     *            The amount of time to wait, in millis
     */
    public void setAccessTimeoutInMillis(long timeInMillis) {
        if (timeInMillis <= 0)
            return;
        this.accessTimeoutInMillis = timeInMillis;
    }

    /**
     * Set the amount of time that is waited for on async methods, in seconds
     *
     * @param timeInSeconds
     *            The amount of time to wait, in seconds
     */
    public void setAccessTimeout(int timeInSeconds) {
        if (timeInSeconds <= 0)
            return;
        this.accessTimeout = timeInSeconds;
        this.accessTimeoutInMillis = timeInSeconds * 1000;
    }

    /**
     * Returns the amount of time that is waited for on async methods, in
     * seconds
     *
     * @return Returns the amount of time in seconds to wait for async methods
     */
    public int getAccessTimeout() {
        long tms = getAccessTimeoutInMillis();
        int i = (int) Math.round(((double) tms * 1.0) / 1000);

        return i;
    }

    /**
     * The amount of time that is waiting on ascync methods that perform
     * modification operations, and wait for the outcome of those operations.
     *
     * @param modificationTimeoutInMillis
     *            The time to wait in milliseconds for modification operations
     *            that wait for the outcome of those operations
     */
    public void setModificationTimeoutInMillis(long modificationTimeoutInMillis) {
        this.modificationTimeoutInMillis = modificationTimeoutInMillis;
    }

    /**
     * Returns the amount of time that is waiting on ascync methods that perform
     * modification operations, and wait for the outcome of those operations.
     *
     * @return the amount of time to wait for modification operations
     */
    public long getModificationTimeoutInMillis() {
        return modificationTimeoutInMillis;
    }

    /**
     * Default amount of time to wait in millis for set operations, rather than
     * just returning. The default is -1, to support backwards compatibility as
     * the old library code would not wait for the set operation to complete.
     *
     * @param setTimeoutInMillis
     */
    public void setSetTimeoutInMillis(long setTimeoutInMillis) {
        this.setTimeoutInMillis = setTimeoutInMillis;
    }

    /**
     * Return the amount of time we should wait for set operations to complete
     * before cancelling them.
     *
     * @return
     */
    public long getSetTimeoutInMillis() {
        return setTimeoutInMillis;
    }



    /**
     * How long to wait in milliseconds for a delete request to occur; before
     * the operation is cancelled.  -1 means that we will not wait for the
     * asynchronous request to complete before returning, the delete will just be
     * fired off.
     *
     * @param removalTimeoutInMillis The amount of time to wait before a delete
     *                               request is cancelled.
     */
    public void setRemovalTimeoutInMillis(long removalTimeoutInMillis) {
        this.removalTimeoutInMillis = removalTimeoutInMillis;
    }

    /**
     * How long we should wait for a delete request to complete.
     *
     * @return
     */
    public long getRemovalTimeoutInMillis() {
        return removalTimeoutInMillis;
    }



    /**
     * The identity of this memcached client.
     *
     * @return
     */
    public UUID getIdentity()
    {
        return id;
    }

    /**
     * Returns the string representation of the Identifiable instance that is
     * assigned to this memcached instance
     */
    public String toString()
    {
        return getIdentity().toString();
    }

    /**
     * The hash code of the Identifiable instance that is assigned to this
     * class at creation time.
     */
    public int hashCode()
    {
        return getIdentity().hashCode();
    }


}

