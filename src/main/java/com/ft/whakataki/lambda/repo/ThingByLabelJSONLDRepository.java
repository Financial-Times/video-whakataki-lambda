package com.ft.whakataki.lambda.repo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import com.ft.whakataki.lambda.thing.service.ThingsJSONLDService;


public class ThingByLabelJSONLDRepository implements Repository {

    ThingsJSONLDService thingService;

    public ThingByLabelJSONLDRepository(ThingsJSONLDService thingService) {
        this.thingService = thingService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        String jsonLd = thingService.getThingByLabel(((ThingsRequest)request).label, ((ThingsRequest)request).type);
        return  ResponseBuilder.create(jsonLd, String.class.getTypeName()).build();
    }
}
