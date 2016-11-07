package com.ft.whakataki.lambda.thing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ThingsLambdaMultiAcceptIntegrationTest {

    private ThingsLambdaMultiAccept thingsLambdaMultiAccept;

    @Test
    public void  testThingById() throws Exception {
        ThingsLambdaMultiAccept thingsLambdaMultiAccept = new ThingsLambdaMultiAccept();

        ThingsRequest thingsRequest = new ThingsRequest();
        thingsRequest.environment = "local";
        thingsRequest.setAccept("application/ld+json");
        thingsRequest.id = "BH";

        // JSON-LD
        ObjectMapper mapper = new ObjectMapper();
        String thingsRequestJSONString = mapper.writeValueAsString(thingsRequest);
        InputStream inputStream = IOUtils.toInputStream(thingsRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thingsLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());

        //JSON
        thingsRequest.setAccept("application/json");
        thingsRequestJSONString = mapper.writeValueAsString(thingsRequest);
        inputStream = IOUtils.toInputStream(thingsRequestJSONString, "UTF-8");
        byteArrayOutputStream = new ByteArrayOutputStream();
        inputStream = IOUtils.toInputStream(thingsRequestJSONString, "UTF-8");
        thingsLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());

    }

    @Test
    public void testThingByLabel() throws Exception {
        ThingsLambdaMultiAccept thingsLambdaMultiAccept = new ThingsLambdaMultiAccept();

        ThingsRequest thingsRequest = new ThingsRequest();
        thingsRequest.environment = "local";
        thingsRequest.setAccept("application/ld+json");
        thingsRequest.label = "HEA";
        thingsRequest.type = "http://www.ft.com/ontology/agent/Role";

        // JSON-LD
        ObjectMapper mapper = new ObjectMapper();
        String thingsRequestJSONString = mapper.writeValueAsString(thingsRequest);
        InputStream inputStream = IOUtils.toInputStream(thingsRequestJSONString, "UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thingsLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());


        //JSON
        thingsRequest.setAccept("application/json");
        thingsRequestJSONString = mapper.writeValueAsString(thingsRequest);
        inputStream = IOUtils.toInputStream(thingsRequestJSONString, "UTF-8");
        byteArrayOutputStream = new ByteArrayOutputStream();
        inputStream = IOUtils.toInputStream(thingsRequestJSONString, "UTF-8");
        thingsLambdaMultiAccept.myHandler(inputStream, byteArrayOutputStream, null);
        System.out.println(byteArrayOutputStream.toString());


    }


    @Test
    @Ignore
    public void infiniteTestThingByLabel() throws Exception {
        while (1==1) {
            testThingByLabel();
            Thread.sleep(50);
        }
    }

}
