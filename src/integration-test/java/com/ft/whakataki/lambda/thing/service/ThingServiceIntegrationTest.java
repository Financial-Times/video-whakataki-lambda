package com.ft.whakataki.lambda.thing.service;

import static java.util.UUID.fromString;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.config.ConfigurationLoader;
import com.ft.whakataki.lambda.thing.model.Thing;
import com.ft.whakataki.lambda.thing.model.Things;
import com.google.common.base.Preconditions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ThingServiceIntegrationTest {


    private static final String ENV = "env";

    private ThingService thingService;


    @Parameters
    public static Collection<Configuration[]> getBlazeGraphRepoConfiguration() {
        String env = System.getProperty(ENV);
        Preconditions.checkNotNull(env,"Environment is null [" + ENV + "]");

        return Arrays.asList(new Configuration[][] {
                { ConfigurationLoader.getBlazeGraphRepositoryConfiguration(env) }
        });
    }

    public ThingServiceIntegrationTest(Configuration configuration) {
        this.thingService = new ThingService(configuration);
    }

    @Test
    public void testThingByUUID() throws Exception {
        Thing thing = this.thingService.getThingByUUID("ca13dde8-e1ac-4e79-9b9a-768aa3b3c0ff");
        System.out.println("Ting [" + thing + "]");
    }


    @Test
    public void testSearchById() throws Exception {
        Thing thing = this.thingService.getThingById("AD");
        System.out.println("Ting [" + thing + "]");
    }


    @Test
    public void testSearchByLabel() throws Exception {
        Things thing = this.thingService.getThingByLabel("BRIT", "http://www.ft.com/ontology/location/Location");
        System.out.println("Ting [" + thing + "]");
    }

}
