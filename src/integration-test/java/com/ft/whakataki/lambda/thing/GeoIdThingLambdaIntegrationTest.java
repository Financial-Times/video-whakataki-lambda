package com.ft.whakataki.lambda.thing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.thing.model.geo.GeoIdRequest;
import com.ft.whakataki.lambda.thing.model.geo.GeoRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class GeoIdThingLambdaIntegrationTest {

    @Test
    public void testGeoByIdJsonLd() throws Exception {
        GeoIdThingLambda geoIdThingLambda = new GeoIdThingLambda();

        GeoIdRequest geoIdRequest = new GeoIdRequest();
        geoIdRequest.environment = "local";
        geoIdRequest.setAccept("application/ld+json");
        geoIdRequest.setId("2647049");
        geoIdRequest.setResourcePath("/geo");

        // JSON-LD
        ObjectMapper mapper = new ObjectMapper();
        String geoRequestJSONString = mapper.writeValueAsString(geoIdRequest);
        InputStream inputStream = IOUtils.toInputStream(geoRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        geoIdThingLambda.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());
    }

    @Test
    public void testGeoByIdJson() throws Exception {
        GeoIdThingLambda geoIdThingLambda = new GeoIdThingLambda();

        GeoIdRequest geoIdRequest = new GeoIdRequest();
        geoIdRequest.environment = "local";
        geoIdRequest.setAccept("application/json");
        geoIdRequest.setId("4932879");
        geoIdRequest.setResourcePath("/geo");

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String geoRequestJSONString = mapper.writeValueAsString(geoIdRequest);
        InputStream inputStream = IOUtils.toInputStream(geoRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        geoIdThingLambda.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());
    }



}
