package com.ft.whakataki.lambda.common;

import java.io.Serializable;


/**
 * Builds resource objects based on the content, and additional meta-data
 */
public class ResponseBuilder {


        private final Serializable content;
        private final String contentType;


        public static ResponseBuilder create(Serializable content) {
            return new ResponseBuilder(content, null);
        }

        public static ResponseBuilder create(Serializable content, String contentType) {
            return new ResponseBuilder(content, contentType);
        }

        /**
         * Create a new resource builder from a piece of content
         */
        private ResponseBuilder(Serializable content, String contentType) {
            this.content = content;
            this.contentType = contentType;

            if (content == null) {
                throw new IllegalArgumentException("A resource cannot have null content");
            }
        }

        /**
         * Build the resource from the supplied properties
         */
        public Response build() {
            return new Response(content, contentType);
        }

    }

