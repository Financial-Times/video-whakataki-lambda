package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.google.common.base.Preconditions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static java.util.UUID.fromString;

/**
 * Created by jem.rayfield on 17/02/2016.
 */
@RunWith(Parameterized.class)
public class ThingServiceJSONLDIntegrationTest {


    private static final String ENV = "env";

    private ThingsServiceJSONLD thingService;


    @Parameterized.Parameters
    public static Collection<Configuration[]> getBlazeGraphRepoConfiguration() {
        String env = System.getProperty(ENV);
        Preconditions.checkNotNull(env,"Environment is null [" + ENV + "]");

        return Arrays.asList(new Configuration[][] {
                    { ConfigurationLoader.getBlazeGraphRepositoryConfiguration(env) }
        });
    }


    public ThingServiceJSONLDIntegrationTest(Configuration configuration) {
        this.thingService = new ThingsServiceJSONLD(configuration);
    }

    @Test
    public void testThingById() throws Exception {
        String jsonLd = this.thingService.getThingById("BH");
        System.out.println("Ting [" + jsonLd + "]");
    }

    @Test
    public void testThingByUUID() throws Exception {
        String jsonLd = this.thingService.getThingByUUID("ca13dde8-e1ac-4e79-9b9a-768aa3b3c0ff");
        System.out.println("Ting [" + jsonLd + "]");
    }

    @Test
    public void testThingByLabel() throws Exception {
        String jsonLd = this.thingService.getThingByLabel("BRIT", null);
        System.out.println("Ting [" + jsonLd + "]");
    }

}
