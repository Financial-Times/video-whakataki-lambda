package com.ft.whakataki.lambda.thing;

import static java.util.UUID.fromString;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.json.JSONUtil;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.service.*;
import org.json.simple.JSONObject;

import java.io.*;

public class ThingLambdaMultiAccept {


    private static final String ENVIRONMENT = "environment";
    private static final String UUID = "thingURI";
    private static final String ACCEPT = "accept";

    /**
     * Why use input and output streams...and not String/POJOs...
     * Well Lambda does some crazy stuff with escaping strings...
     *
     */
    public void myHandler(InputStream inputStream, OutputStream os, Context context) {
        JSONObject requestJson = JSONUtil.getJsonFromInputStream(inputStream);
        Configuration blazeGraphConfig = ConfigurationLoader.getBlazeGraphRepositoryConfiguration(requestJson.get(ENVIRONMENT).toString());
        try {
            String result;
            if (requestJson.get(ACCEPT).toString().equals("application/ld+json")) {
                ThingJSONLDService thingJSONLDService = new ThingsServiceJSONLD(blazeGraphConfig);
                if (blazeGraphConfig.getUseCache())
                    thingJSONLDService = new CachedThingJSONLDService(blazeGraphConfig, thingJSONLDService, blazeGraphConfig.getUseLocalCache());
                result = thingJSONLDService.getThingByUUID(requestJson.get(UUID).toString());
            } else {
                ThingServiceI thingServiceById = new ThingService(blazeGraphConfig);
                if (blazeGraphConfig.getUseCache())
                    thingServiceById = new CachedThingService(blazeGraphConfig, thingServiceById, blazeGraphConfig.getUseLocalCache());
                Thing thing = thingServiceById.getThingByUUID(requestJson.get(UUID).toString());
                ObjectMapper mapper = new ObjectMapper();
                result = mapper.writeValueAsString(thing);
            }
            if ((result == null) || (result.equals("null")))
                throw new ThingNotFoundException("404 - Thing Not Found [" + requestJson.get(UUID) + "]");
            os.write(result.getBytes());
            os.close();
            inputStream.close();
        } catch (IOException io) {
            throw new ThingLambdaException("Server Error, unable to write ld+json output stream", io);
        } catch (OutOfMemoryError outOfMemoryError) {
            System.exit(0);
        }
    }





}
