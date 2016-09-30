package com.ft.whakataki.lambda.repo;


import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.ThingRequest;
import com.ft.whakataki.lambda.thing.service.ThingService;
import com.ft.whakataki.lambda.thing.service.ThingServiceI;

import java.util.UUID;

public class ThingByUUIDRepository implements Repository {

    ThingServiceI thingService;

    public ThingByUUIDRepository(ThingServiceI thingService) {
        this.thingService = thingService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        Thing thing = thingService.getThingByUUID(((ThingRequest)request).thingURI);
        return  ResponseBuilder.create(thing, Thing.class.getTypeName()).build();
    }

}
