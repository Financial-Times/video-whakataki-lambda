package com.ft.whakataki.lambda.common;

import java.io.Serializable;

public class Response implements Serializable {


    private static final long serialVersionUID = 7523405679515893249L;

    private Serializable content;
    private String contentType;

    public static final Response NOT_FOUND_RESOURCE = new Response();

    private Response() {

    }

    Response(Serializable content, String contentType ) {
        this.content = content;
        this.setContentType(contentType);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContent(Class<T> contentClass) {
        if (content == null)
            return null;
        if (!contentClass.isAssignableFrom(content.getClass()))
            throw new RuntimeException("A resource's content of type "
                    + contentClass.getSimpleName()
                    + " was requested, but is not supported");
        return (T) content;
    }

    public void setContent(Serializable content) {
        this.content = content;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
