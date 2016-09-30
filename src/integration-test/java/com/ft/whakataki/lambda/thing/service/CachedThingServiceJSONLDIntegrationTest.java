package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.repo.ThingByIdJSONLDRepository;
import com.google.common.base.Preconditions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@RunWith(Parameterized.class)
public class CachedThingServiceJSONLDIntegrationTest {

    private static final String ENV = "env";

    private ThingsServiceJSONLD thingService;
    private Repository thingByIdRepository;
    private CachedThingsJSONLDService cachedThingByIdService;
    private CachedThingJSONLDService cachedThingService;

    @Parameterized.Parameters
    public static Collection<Configuration[]> getBlazeGraphRepoConfiguration() {
        String env = System.getProperty(ENV);
        Preconditions.checkNotNull(env,"Environment is null [" + ENV + "]");

        return Arrays.asList(new Configuration[][] {
                { ConfigurationLoader.getBlazeGraphRepositoryConfiguration(env) }
        });
    }

    public CachedThingServiceJSONLDIntegrationTest(Configuration configuration) {
        this.thingService = new ThingsServiceJSONLD(configuration);
        this.thingByIdRepository = new ThingByIdJSONLDRepository(this.thingService );
        this.cachedThingByIdService = new CachedThingsJSONLDService(configuration, this.thingByIdRepository, thingService, configuration.getUseLocalCache());
        this.cachedThingService = new CachedThingJSONLDService(configuration, thingService, configuration.getUseLocalCache());
    }

    @Test
    public void testCachedThingByIdService() throws Exception {
        // configuration.getUseLocalCache()
        String jsonLd = this.cachedThingByIdService.getThingById("BH");
        System.out.println(jsonLd);
    }

   @Test
    public void testCachedThingService() throws Exception {
       String jsonLd = this.cachedThingService.getThingByUUID("ca13dde8-e1ac-4e79-9b9a-768aa3b3c0ff");
       System.out.println(jsonLd);
    }
}
