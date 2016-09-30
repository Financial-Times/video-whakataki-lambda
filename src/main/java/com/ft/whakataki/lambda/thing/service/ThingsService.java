package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.Things;


public interface ThingsService {

    Thing getThingById(String id);

    Things getThingByLabel(String label, String type);
}
