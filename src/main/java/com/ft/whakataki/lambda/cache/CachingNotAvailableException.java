package com.ft.whakataki.lambda.cache;

public class CachingNotAvailableException extends RuntimeException {

    public CachingNotAvailableException(Throwable cause) {
        super("Caching Not Avalible", cause);
    }

}

