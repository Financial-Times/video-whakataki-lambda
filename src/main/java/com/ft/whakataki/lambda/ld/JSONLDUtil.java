package com.ft.whakataki.lambda.ld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.SesameRDFParser;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jem.rayfield on 18/02/2016.
 */
public class JSONLDUtil {

    private static final String contextJsonFile = loadFileAsString("/ld/thing.ld-context");

    public static String convertThingModelToJsonLD(Model thingModel) {
        try {
            Object json = JsonLdProcessor.fromRDF(thingModel, new SesameRDFParser());

            Map contextJson = new ObjectMapper().readValue(contextJsonFile, HashMap.class);
            return JsonUtils.toPrettyString(JsonLdProcessor.compact(json, contextJson, new JsonLdOptions()));
        } catch(JsonLdError jle) {
            throw new ThingLambdaException("Problem creating JSON-LD", jle);
        } catch(IOException io) {
            throw new ThingLambdaException("Problem creating JSON-LD", io);
        }
    }

    private static String loadFileAsString(String filename) {
        try {
            return IOUtils.toString(JSONLDUtil.class.getResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
