package com.ft.whakataki.lambda.thing;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.json.JSONUtil;
import com.ft.whakataki.lambda.repo.geo.GeoJSONLDRepository;
import com.ft.whakataki.lambda.repo.geo.GeoJSONRepository;
import com.ft.whakataki.lambda.thing.exception.GeoIdNotFoundException;
import com.ft.whakataki.lambda.thing.exception.ThingBadSearchRequestException;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.ft.whakataki.lambda.thing.service.geo.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoThingLambda {

    private static final String ENVIRONMENT = "environment";
    private static final String SEARCH_STRING = "searchString";
    private static final String ACCEPT = "accept";
    private static final String SEARCH_WILD_CARD = "*";

    private static final String BAD_SEARCH = "400 - Bad Request - Search invalid:";


    private static final Integer SEARCH_TOKEN_MIN_LENGTH = 4;

    public void myHandler(InputStream inputStream, OutputStream os, Context context) {
        JSONObject requestJson = JSONUtil.getJsonFromInputStream(inputStream);
        String searchString = validSearchString(requestJson.get(SEARCH_STRING).toString());

        if (searchString.equals("")) {
            throw new GeoIdNotFoundException(BAD_SEARCH + " search must include searchString");
        }

        Configuration blazeGraphConfig = ConfigurationLoader.getBlazeGraphRepositoryConfiguration(requestJson.get(ENVIRONMENT).toString());
        try {
            String result;
            if (requestJson.get(ACCEPT).toString().equals("application/ld+json")) {
                GeoJSONLdService geoJSONLdService = new RepoGeoJSONLdService(blazeGraphConfig);

                Repository geoBySearchLabelJSONLdRepo = new GeoJSONLDRepository(geoJSONLdService);
                if (blazeGraphConfig.getUseCache())
                    geoJSONLdService = new CachedGeoJSONLdService(blazeGraphConfig, geoBySearchLabelJSONLdRepo, geoJSONLdService, blazeGraphConfig.getUseLocalCache());
                result = geoJSONLdService.getGeoByLabel(searchString);

            } else {
                ObjectMapper mapper = new ObjectMapper();

                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                //mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

                GeoJSONService geoJSONService = new RepoGeoJSONService(blazeGraphConfig);

                Repository geoBySearchLabelJSONRepo = new GeoJSONRepository(geoJSONService);
                if (blazeGraphConfig.getUseCache()) {
                    geoJSONService = new CachedGeoJSONService(blazeGraphConfig, geoBySearchLabelJSONRepo, geoJSONService, blazeGraphConfig.getUseLocalCache());
                }
                result = mapper.writeValueAsString(geoJSONService.getGeoByLabel(searchString));
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

    private void checkSearchTokensValid(String word, String searchString) {
        Pattern pattern = Pattern.compile("(\\*)");
        Matcher matcher = pattern.matcher(word);

        int numberOfWildCards = 0;
        while (matcher.find()) {
            numberOfWildCards++;
        }
        if (numberOfWildCards > 1 ) {
            throw new ThingBadSearchRequestException(BAD_SEARCH + " Search token [" + word + "] within the search string [" + searchString + "] can only include a single trailing " + SEARCH_WILD_CARD + " wildcard supporting starts with");
        }
        if (numberOfWildCards == 1 ) {
            if (word.length() < SEARCH_TOKEN_MIN_LENGTH )
                throw new ThingBadSearchRequestException(BAD_SEARCH + " Search token [" + word + "] length [" + word.length() + "] must be [" + SEARCH_TOKEN_MIN_LENGTH + "] or more characters" );

            if ( !word.substring(word.length() - 1).equals(SEARCH_WILD_CARD) ) {
                throw new ThingBadSearchRequestException(BAD_SEARCH + " Search token [" + word + "] within the search string [" + searchString + "] must only include trailing wildcards supporting starts with");
            }
        }
    }
    public String validSearchString(String searchString) {
        final String compactedSearchString = searchString.replaceAll("\\s+", " ").trim();

        List<String> words = Arrays.asList(compactedSearchString.split("\\s+"));
        words.forEach(word -> checkSearchTokensValid(word, compactedSearchString));

        return compactedSearchString;
    }

}
