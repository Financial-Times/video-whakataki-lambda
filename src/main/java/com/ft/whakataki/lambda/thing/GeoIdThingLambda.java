package com.ft.whakataki.lambda.thing;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.json.JSONUtil;
import com.ft.whakataki.lambda.repo.geo.GeoIdJSONLDRepository;
import com.ft.whakataki.lambda.repo.geo.GeoIdJSONRepository;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.ft.whakataki.lambda.thing.service.geo.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GeoIdThingLambda {

    private static final String ENVIRONMENT = "environment";
    private static final String ID = "id";
    private static final String ACCEPT = "accept";

    private static final String BAD_SEARCH = "400 - Bad Request - Search invalid:";

    public void myHandler(InputStream inputStream, OutputStream os, Context context) {
        JSONObject requestJson = JSONUtil.getJsonFromInputStream(inputStream);
        String geoId = requestJson.get(ID).toString();

        Configuration blazeGraphConfig = ConfigurationLoader.getBlazeGraphRepositoryConfiguration(requestJson.get(ENVIRONMENT).toString());
        try {
            String result;
            if (requestJson.get(ACCEPT).toString().equals("application/ld+json")) {
                GeoIdJSONLDService geoIdJSONLdService = new RepoGeoIdJSONLdService(blazeGraphConfig);

                Repository geoIdJSONLdRepo = new GeoIdJSONLDRepository(geoIdJSONLdService);
                if (blazeGraphConfig.getUseCache())
                    geoIdJSONLdService = new CachedGeoIdJSONLdService(blazeGraphConfig, geoIdJSONLdRepo, geoIdJSONLdService, blazeGraphConfig.getUseLocalCache());
                result = geoIdJSONLdService.getGeoById(geoId);

            } else {
                ObjectMapper mapper = new ObjectMapper();

                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                GeoIdJSONService geoIdJSONService = new RepoGeoIdJSONService(blazeGraphConfig);

                Repository geoBySearchLabelJSONRepo = new GeoIdJSONRepository(geoIdJSONService);
                if (blazeGraphConfig.getUseCache()) {
                    geoIdJSONService = new CachedGeoIdJSONService(blazeGraphConfig, geoBySearchLabelJSONRepo, geoIdJSONService, blazeGraphConfig.getUseLocalCache());
                }
                result = mapper.writeValueAsString(geoIdJSONService.getGeoById(geoId));
            }
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
