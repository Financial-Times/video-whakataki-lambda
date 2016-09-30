package com.ft.whakataki.lambda.thing.service;

/**
 * Created by jem.rayfield on 19/02/2016.
 */
public interface ThingsJSONLDService {

    public String getThingById(String id);

    public String getThingByLabel(String label, String type);
}
