package com.ft.whakataki.lambda.thing.exception;

import java.util.UUID;

public class ThingBadSearchRequestException extends ThingLambdaException {

    public ThingBadSearchRequestException(String message, String uuid) {
        super(message, uuid);
    }

    public ThingBadSearchRequestException(String message) {
        super(message);
    }

}
