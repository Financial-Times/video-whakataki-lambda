package com.ft.whakataki.lambda.thing.exception;


import java.util.UUID;

public class ThingNotFoundException extends ThingLambdaException {

    public ThingNotFoundException(String message, String uuid) {
        super(message, uuid);
    }

    public ThingNotFoundException(String message) {
        super(message);
    }

}
