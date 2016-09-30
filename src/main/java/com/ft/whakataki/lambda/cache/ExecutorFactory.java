package com.ft.whakataki.lambda.cache;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorFactory {

    private static final int DEFAULT_MAX_POOL_SIZE = 50;
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_KEEP_ALIVE_TIME_IN_SECONDS = 10*60;
    private static final int DEFAULT_QUEUE_CAPACITY = 20;

    private ThreadPoolExecutor threadPoolExecutor;

    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveInSeconds;
    private int queueCapacity;

    public ExecutorFactory() {
        corePoolSize = DEFAULT_CORE_POOL_SIZE;
        maxPoolSize = DEFAULT_MAX_POOL_SIZE;
        queueCapacity = DEFAULT_QUEUE_CAPACITY;
        keepAliveInSeconds = DEFAULT_KEEP_ALIVE_TIME_IN_SECONDS;
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveInSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity));
    }

    public ExecutorService getExecutor() {
        return threadPoolExecutor;
    }

    public void destroy() {
        threadPoolExecutor.shutdown();
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setKeepAliveInSeconds(int keepAliveInSeconds) {
        this.keepAliveInSeconds = keepAliveInSeconds;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

}

