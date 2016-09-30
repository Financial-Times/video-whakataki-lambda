package com.ft.whakataki.lambda.thing;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.ThingRequest;
import com.ft.whakataki.lambda.thing.service.CachedThingService;
import com.ft.whakataki.lambda.thing.service.ThingService;

import java.util.UUID;


public class ThingLambda implements RequestHandler<ThingRequest, Thing> {

        public Thing handleRequest(ThingRequest thing, Context context) {
            Configuration blazeGraphConfig = ConfigurationLoader.getBlazeGraphRepositoryConfiguration(thing.environment);
            ThingService service = new ThingService(blazeGraphConfig);
            Thing result;
            if (blazeGraphConfig.getUseCache()) {
                CachedThingService cachedThingService = new CachedThingService(blazeGraphConfig, service, blazeGraphConfig.getUseLocalCache());
                result = cachedThingService.getThingByUUID(thing.thingURI);
            } else {
                result = service.getThingByUUID(thing.thingURI);
            }
            if (result == null)
                throw new ThingNotFoundException("404 - Thing Not Found" + thing);
            return result;
        }

}
