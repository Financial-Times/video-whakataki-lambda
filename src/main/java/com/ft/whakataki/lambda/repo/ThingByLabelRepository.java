package com.ft.whakataki.lambda.repo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.Things;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import com.ft.whakataki.lambda.thing.service.ThingsService;

public class ThingByLabelRepository implements Repository{

    ThingsService thingService;

    public ThingByLabelRepository(ThingsService thingService) {
        this.thingService = thingService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        Things thing = thingService.getThingByLabel(((ThingsRequest)request).label, ((ThingsRequest)request).type);
        return  ResponseBuilder.create(thing, Thing.class.getTypeName()).build();
    }
}
