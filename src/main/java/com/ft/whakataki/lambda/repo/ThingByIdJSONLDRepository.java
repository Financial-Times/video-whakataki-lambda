package com.ft.whakataki.lambda.repo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import com.ft.whakataki.lambda.thing.service.ThingsJSONLDService;
import com.ft.whakataki.lambda.thing.service.ThingsServiceJSONLD;


public class ThingByIdJSONLDRepository implements Repository {

    ThingsJSONLDService thingService;

    public ThingByIdJSONLDRepository(ThingsJSONLDService thingService) {
        this.thingService = thingService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        String jsonLd = thingService.getThingById(((ThingsRequest)request).id);
        return  ResponseBuilder.create(jsonLd, String.class.getTypeName()).build();
    }
}
