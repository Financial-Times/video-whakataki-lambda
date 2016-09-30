package com.ft.whakataki.lambda.thing;

import com.amazonaws.services.lambda.runtime.Context;
import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.repo.ThingByIdRepository;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.ThingsRequest;
import com.ft.whakataki.lambda.thing.service.CachedThingsService;
import com.ft.whakataki.lambda.thing.service.ThingService;

public class ThingsByIdLambda {

    public Thing handleRequest(ThingsRequest thing, Context context) {
        Configuration blazeGraphConfig = ConfigurationLoader.getBlazeGraphRepositoryConfiguration(thing.environment);
        ThingService thingService = new ThingService(blazeGraphConfig);
        Repository thingByIdRepository = new ThingByIdRepository(thingService);
        Thing result;
        if (blazeGraphConfig.getUseCache()) {
            CachedThingsService cachedThingService = new CachedThingsService(blazeGraphConfig, thingByIdRepository, thingService, blazeGraphConfig.getUseLocalCache());
            result = cachedThingService.getThingById(thing.id);
        } else {
            result = thingService.getThingById(thing.id);
        }
        if (result == null)
            throw new ThingNotFoundException("404 - Thing Not Found" + thing);
        return result;
    }

}
