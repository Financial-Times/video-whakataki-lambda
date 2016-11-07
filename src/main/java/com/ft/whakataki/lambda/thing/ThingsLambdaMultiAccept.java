package com.ft.whakataki.lambda.thing;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.json.JSONUtil;
import com.ft.whakataki.lambda.repo.ThingByIdJSONLDRepository;
import com.ft.whakataki.lambda.repo.ThingByIdRepository;
import com.ft.whakataki.lambda.repo.ThingByLabelJSONLDRepository;
import com.ft.whakataki.lambda.repo.ThingByLabelRepository;
import com.ft.whakataki.lambda.thing.exception.ThingBadSearchRequestException;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.service.*;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ThingsLambdaMultiAccept {

    private static final String ENVIRONMENT = "environment";
    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String TYPE = "type";
    private static final String ACCEPT = "accept";
    private static final String SEARCH_WILD_CARD = "*";

    private static final String BAD_SEARCH = "400 - Bad Request - Search invalid:";


    private static final Integer SEARCH_TOKEN_MIN_LENGTH = 4;


    private void checkIdAndLabelSearch(String id, String label) {
        if ((id.length()>0) && (label.length()>0))
            throw new ThingBadSearchRequestException("400 - An id and label search is not supported");
    }

    private void checkNoSearch(String id, String label) {
        if ((id.length()==0) && (label.length()==0))
            throw new ThingBadSearchRequestException("400 - A label or id is required to search");
    }


    private void checkParams(String id, String label, String type) {
        checkNoSearch(id, label);
        checkIdAndLabelSearch(id, label);

    }

    /**
     * Why use input and output streams...and not >String/POJOs...
     * Well Lambda does some crazy stuff with escaping strings...
     *
     */
    public void myHandler(InputStream inputStream, OutputStream os, Context context) {
        JSONObject requestJson = JSONUtil.getJsonFromInputStream(inputStream);
        Configuration blazeGraphConfig = ConfigurationLoader.getBlazeGraphRepositoryConfiguration(requestJson.get(ENVIRONMENT).toString());

        String id = requestJson.get(ID)==null? "" : requestJson.get(ID).toString();
        String label = requestJson.get(LABEL)==null? "" : requestJson.get(LABEL).toString();
        String type = requestJson.get(TYPE)==null? "" : requestJson.get(TYPE).toString();

        checkParams(id, label, type);

        label = validSearchString(label);

        String result = "";
        try {
            if (requestJson.get(ACCEPT).toString().equals("application/ld+json")) {
                ThingsJSONLDService thingsJSONLDService = new ThingsServiceJSONLD(blazeGraphConfig);
                if (label.length()>=1) {
                    Repository thingByLabelRepository = new ThingByLabelJSONLDRepository(thingsJSONLDService);
                    if (blazeGraphConfig.getUseCache())
                        thingsJSONLDService = new CachedThingsJSONLDService(blazeGraphConfig, thingByLabelRepository, thingsJSONLDService, blazeGraphConfig.getUseLocalCache());
                    result = thingsJSONLDService.getThingByLabel(label, type);
                } else {
                    Repository thingByIdRepository = new ThingByIdJSONLDRepository(thingsJSONLDService);
                    if (blazeGraphConfig.getUseCache())
                        thingsJSONLDService = new CachedThingsJSONLDService(blazeGraphConfig, thingByIdRepository, thingsJSONLDService, blazeGraphConfig.getUseLocalCache());
                    result = thingsJSONLDService.getThingById(id);

                }
            } else {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
                ThingsService thingService = new ThingService(blazeGraphConfig);
                if (label.length()>=1) {
                    Repository thingByLabelRepository = new ThingByLabelRepository(thingService);
                    if (blazeGraphConfig.getUseCache())
                        thingService = new CachedThingsService(blazeGraphConfig, thingByLabelRepository, thingService, blazeGraphConfig.getUseLocalCache());
                    result = mapper.writeValueAsString(thingService.getThingByLabel(label, type));
                } else {
                    Repository thingByIdRepository = new ThingByIdRepository(thingService);
                    if (blazeGraphConfig.getUseCache())
                        thingService = new CachedThingsService(blazeGraphConfig, thingByIdRepository, thingService, blazeGraphConfig.getUseLocalCache());
                    result = mapper.writeValueAsString(thingService.getThingById(id));
                }

            }
            os.write(result.getBytes());
            os.close();
            inputStream.close();
        } catch (IOException io) {
            throw new ThingLambdaException("Server Error, unable to write ld+json output stream", io);
        } catch (OutOfMemoryError outOfMemoryError) {
            System.exit(0);
        }
        if (result == null)
            throw new ThingNotFoundException("404 - Thing Not Found" + requestJson.get(ID));
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
