package com.ft.whakataki.lambda.thing;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.thing.model.ThingRequest;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class ThingLambdaMultiAcceptIntegrationTest {


    @Ignore
    @Test
    public void  testThingByUUID() throws Exception {
        ThingLambdaMultiAccept thingLambdaMultiAccept = new ThingLambdaMultiAccept();

        ThingRequest thingRequest = new ThingRequest();
        thingRequest.environment = "local";
        thingRequest.setAccept("application/ld+json");
        thingRequest.thingURI = "82ee7bdc-be91-4c80-acec-0c0a0c94abf1";

        // JSON-LD
        ObjectMapper mapper = new ObjectMapper();
        String thingRequestJSONString = mapper.writeValueAsString(thingRequest);
        InputStream inputStream = IOUtils.toInputStream(thingRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thingLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());


        //JSON
        thingRequest.setAccept("application/json");
        thingRequestJSONString = mapper.writeValueAsString(thingRequest);
        inputStream = IOUtils.toInputStream(thingRequestJSONString, "UTF-8");
        byteArrayOutputStream = new ByteArrayOutputStream();
        inputStream = IOUtils.toInputStream(thingRequestJSONString, "UTF-8");
        thingLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());

        thingRequest.setAccept("application/json");
        thingRequestJSONString = mapper.writeValueAsString(thingRequest);
        inputStream = IOUtils.toInputStream(thingRequestJSONString, "UTF-8");
        byteArrayOutputStream = new ByteArrayOutputStream();
        inputStream = IOUtils.toInputStream(thingRequestJSONString, "UTF-8");
        thingLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());

    }

}
