package com.ft.whakataki.lambda.thing.exception;

import java.util.UUID;


public class ThingLambdaException extends RuntimeException {

    private final String uuid;

    public ThingLambdaException(final String message) {
        super(message);
        uuid = null;
    }

    public ThingLambdaException(final String message, final Throwable cause) {
        this(message, cause, null);
    }

    public ThingLambdaException(final String message, final String uuid) {
        this(message, null, uuid);
    }


    public ThingLambdaException(final String message, final Throwable cause, final String uuid) {
        super(message, cause);
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

}
