package com.ft.whakataki.lambda.thing.service;

import static java.util.UUID.fromString;

import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import static org.fest.assertions.Assertions.assertThat;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.Thing;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openrdf.model.*;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;


import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThingServiceTest {

    private static final String THING_UUID = "00000000-0000-0000-0000-000000000000";
    private static final String BASE_URI = "http://api.ft.com/things/";
    private static final String THING_URI = "http://api.ft.com/things/" + THING_UUID;
    private static final String PREF_LABEL = "http://www.ft.com/ontology/thing/prefLabel";
    private static final String THING_CLASS = "http://www.ft.com/ontology/thing/Thing";
    private static final String RDF_TYPE = "rdf:type";

    private final String NAMESPACE_URL = "whakataki";
    private final String SERVICE_URL = "http://blazegraph-1791397694.eu-west-1.elb.amazonaws.com//blazegraph";
    private final String API_GW = "p8c0zf0s6d.execute-api.eu-west-1.amazonaws.com/dev";

    private final BlazeGraphNameSpaceRepo blazeGraphNameSpaceRepo = mock(BlazeGraphNameSpaceRepo.class);
    private final Configuration configuration = mock(Configuration.class);
    private final RemoteRepositoryManager remoteRepositoryManager = mock(RemoteRepositoryManager.class);
    private final RemoteRepository remoteRepository = mock(RemoteRepository.class);

    private Model model = mock(Model.class);
    private ValueFactory valueFactory = new ValueFactoryImpl();
    private ThingService thingService;

    @Before
    public void setupUp() throws Exception {
        model = new LinkedHashModel();
        when(configuration.getServiceURL()).thenReturn(SERVICE_URL);
        when(configuration.getNamespace()).thenReturn(NAMESPACE_URL);
        when(configuration.getApiGw()).thenReturn(API_GW);
        when(blazeGraphNameSpaceRepo.getConfiguration()).thenReturn(configuration);
        when(blazeGraphNameSpaceRepo.getRemoteRepositoryManager()).thenReturn(remoteRepositoryManager);
        when(remoteRepositoryManager.getRepositoryForNamespace(NAMESPACE_URL)).thenReturn(remoteRepository);
        when(blazeGraphNameSpaceRepo.executeGraphQuery(anyString())).thenReturn(model);
        thingService = new ThingService(blazeGraphNameSpaceRepo);
        thingService.setConfiguration(configuration);
    }

    @Test(expected = ThingNotFoundException.class)
    public void readThingByUuidNotFound() throws Exception {
        thingService.getThingByUUID(THING_UUID);
    }

    @Test(expected = NullPointerException.class)
    public void readThingByUuidNull() throws Exception {
        thingService.getThingByUUID(null);
    }


    @Test
    public void readThingByUuidReturnsThingUri() throws Exception {
        model.add(statement(THING_URI, PREF_LABEL, literal("Jem Rayfield")));
        model.add(statement(THING_URI, RDF_TYPE, uri(THING_CLASS)));
        Thing thing = thingService.getThingByUUID(THING_UUID);

        assertThat(thing.thingURI).isEqualTo(thingService.subsHostResouce(THING_URI));
    }

    @Test
    public void returnsPrefLabel() throws Exception {
        String label = "Test Label";
        model.add(statement(THING_URI, PREF_LABEL, literal(label)));
        Thing thing = thingService.getThingByUUID(THING_UUID);
        assertThat(thing.prefLabel).isEqualTo(label);
    }

    private StatementImpl statement(String subject, String predicate, Value object) {
        return new StatementImpl(uri(subject), uri(predicate), object);
    }

    private Literal literal(String literal) {
        return valueFactory.createLiteral(literal);
    }

    private URI uri(String uri) {
        return valueFactory.createURI(uri);
    }

}
