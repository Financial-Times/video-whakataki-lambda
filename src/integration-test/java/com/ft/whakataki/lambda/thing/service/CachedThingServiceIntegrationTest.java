package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.repo.ThingByIdRepository;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.google.common.base.Preconditions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@RunWith(Parameterized.class)
public class CachedThingServiceIntegrationTest {

    private static final String ENV = "env";

    private ThingService thingService;
    private CachedThingsService cachedThingsService;
    private CachedThingService cachedThingService;
    private Repository thingByIdRepository;

    @Parameterized.Parameters
    public static Collection<Configuration[]> getBlazeGraphRepoConfiguration() {
        String env = System.getProperty(ENV);
        Preconditions.checkNotNull(env,"Environment is null [" + ENV + "]");

        return Arrays.asList(new Configuration[][] {
                { ConfigurationLoader.getBlazeGraphRepositoryConfiguration(env) }
        });
    }

    public CachedThingServiceIntegrationTest(Configuration configuration) {
        this.thingService = new ThingService(configuration);
        this.thingByIdRepository = new ThingByIdRepository(thingService);
        this.cachedThingsService = new CachedThingsService(configuration, thingByIdRepository, thingService, configuration.getUseLocalCache());
        this.cachedThingService = new CachedThingService(configuration, thingService, configuration.getUseLocalCache());
    }

    @Test
    public void testCachedThingByIdService() throws Exception {
        // configuration.getUseLocalCache()
        Thing thing = this.cachedThingsService.getThingById("BH");
        System.out.println("Ting [" + thing + "]");
    }

    @Test
    public void testCachedThingService() throws Exception {
        Thing thing = this.cachedThingService.getThingByUUID("ca13dde8-e1ac-4e79-9b9a-768aa3b3c0ff");
        System.out.println("Ting [" + thing + "]");
    }

}


