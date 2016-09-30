package com.ft.whakataki.lambda.json;

import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONUtil {


    public static JSONObject getJsonFromInputStream(InputStream inputStream) {
        JSONParser jsonParser = new JSONParser();
        try {
            return (JSONObject) jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));

        } catch (UnsupportedEncodingException use) {
            throw new ThingLambdaException("Unable to parse the Input stream into JSON", use);
        } catch (ParseException pe) {
            throw new ThingLambdaException("Unable to parse the Input stream into JSON", pe);
        } catch (IOException io) {
            throw new ThingLambdaException("Unable to parse the Input stream into JSON", io);
        }
    }

}
