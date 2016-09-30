package com.ft.whakataki.lambda.cache;


import com.ft.whakataki.lambda.common.Response;

public class LookupResult {

    enum Type {
        NOT_READY, NOT_FOUND, FOUND, FAILURE
    }

    public static LookupResult notReady() {
        return new LookupResult(Type.NOT_READY, null);
    }

    public static LookupResult notFound() {
        return new LookupResult(Type.NOT_FOUND, null);
    }

    public static LookupResult found(Response response) {
        return new LookupResult(response);
    }

    public static LookupResult failure(Throwable cause) {
        return new LookupResult(Type.FAILURE, cause);
    }

    private final Response response;

    private final Type type;

    private Throwable cause;

    private LookupResult(Response response) {
        this.response = response;
        this.type = Type.FOUND;
    }

    private LookupResult(Type type, Throwable cause) {
        this.cause = cause;
        this.response = null;
        this.type = type;
    }

    public Response getResponse() {
        return response;
    }

    public boolean isFound() {
        return type == Type.FOUND;
    }

    public boolean isReady() {
        return type != Type.NOT_READY;
    }

    public boolean isFailure() {
        return type == Type.FAILURE;
    }

    public Throwable getCause() {
        return cause;
    }

}
