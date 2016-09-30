package com.ft.whakataki.lambda.repo;


import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import com.ft.whakataki.lambda.thing.service.ThingsService;

public class ThingByIdRepository implements Repository {

    ThingsService thingService;

    public ThingByIdRepository(ThingsService thingService) {
        this.thingService = thingService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        Thing thing = thingService.getThingById(((ThingsRequest)request).id);
        return  ResponseBuilder.create(thing, Thing.class.getTypeName()).build();
    }

}
