package com.ft.whakataki.lambda.repo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.ThingRequest;
import com.ft.whakataki.lambda.thing.service.ThingJSONLDService;
import com.ft.whakataki.lambda.thing.service.ThingsServiceJSONLD;

import java.util.UUID;

public class ThingByUUIDJSONLDRepository implements Repository {

    ThingJSONLDService thingService;

    public ThingByUUIDJSONLDRepository(ThingJSONLDService thingService) {
        this.thingService = thingService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        String jsonLd = thingService.getThingByUUID(((ThingRequest)request).thingURI);
        return  ResponseBuilder.create(jsonLd, Thing.class.getTypeName()).build();
    }

}
