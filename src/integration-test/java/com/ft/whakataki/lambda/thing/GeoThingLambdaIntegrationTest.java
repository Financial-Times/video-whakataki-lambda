package com.ft.whakataki.lambda.thing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.thing.model.geo.GeoRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GeoThingLambdaIntegrationTest {

    @Test
    public void testGeoBySearchLabelJsonLd() throws Exception {
        GeoThingLambda geoThingLambda = new GeoThingLambda();

        GeoRequest geoRequest = new GeoRequest();
        geoRequest.environment = "staging";
        geoRequest.setAccept("application/ld+json");
        geoRequest.setSearchString("Germany");
        geoRequest.setResourcePath("/geo");

        // JSON-LD
        ObjectMapper mapper = new ObjectMapper();
        String geoRequestJSONString = mapper.writeValueAsString(geoRequest);
        InputStream inputStream = IOUtils.toInputStream(geoRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        geoThingLambda.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());
    }


    @Test
    public void testGeoBySearchLabelJson() throws Exception {
        GeoThingLambda geoThingLambda = new GeoThingLambda();

        GeoRequest geoRequest = new GeoRequest();
        geoRequest.environment = "staging";
        geoRequest.setAccept("application/json");
        geoRequest.setSearchString("Germany");
        geoRequest.setResourcePath("/geo");

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String geoRequestJSONString = mapper.writeValueAsString(geoRequest);
        InputStream inputStream = IOUtils.toInputStream(geoRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        geoThingLambda.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());
    }

}
