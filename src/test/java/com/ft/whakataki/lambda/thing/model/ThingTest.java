package com.ft.whakataki.lambda.thing.model;


import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ThingTest {

    private static final UUID FT_THING_UUID = fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID EQUIV_THING_ONE_UUID = fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID EQUIV_THING_TWO_UUID = fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID AGENT_ROLE_UUID = fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID PROVENANCE_UUID = fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID AGENT_UUID = fromString("00000000-0000-0000-0000-000000000005");
    private static final UUID ROLE_UUID = fromString("00000000-0000-0000-0000-000000000006");
    private static final String BASE_URI = "http://api.ft.com/things/";
    private static final String FT_THING_URI = "http://api.ft.com/things/" + FT_THING_UUID;
    private static final String EQUIV_THING_ONE_URI = "http://api.ft.com/things/" +  EQUIV_THING_ONE_UUID;
    private static final String EQUIV_THING_TWO_URI = "http://api.ft.com/things/" +  EQUIV_THING_TWO_UUID;
    private static final String AGENT_ROLE_URI = "http://api.ft.com/things/" +  AGENT_ROLE_UUID;
    private static final String PROVENANCE_URI = "http://api.ft.com/things/" +  PROVENANCE_UUID;
    private static final String AGENT_URI = "http://api.ft.com/things/" +  AGENT_UUID;
    private static final String ROLE_URI = "http://api.ft.com/things/" +  ROLE_UUID;
    private static final String PREF_LABEL = "jems lovely label";

    private static final String THING_AS_STRING = "{ \"thingURI\": \"" + FT_THING_URI + "\", "+
                                                    "\"prefLabel\": \"" + PREF_LABEL + "\", "+
                                                    "\"identifierValue\": \"\", " +
                                                    "\"provenance\": \"\", " +
                                                    "\"equivalent\": [ "+
                                                         "{ \"thingURI\": \""+ EQUIV_THING_ONE_URI + "\", " +
                                                           "\"prefLabel\": \"" + PREF_LABEL + " One\", " +
                                                           "\"identifierValue\": \"1\", " +
                                                           "\"provenance\": { \"atTime\": \"0012-12-12T12:12:12.000-00:01:15\", agentLabel\": \"Jem Rayfield\", roleLabel\": \"Manager\" }}, " +
                                                         "{ \"thingURI\": \""+ EQUIV_THING_TWO_URI + "\", " +
                                                           "\"prefLabel\": \"" + PREF_LABEL + " Two\", " +
                                                           "\"identifierValue\": \"2\", " +
                                                           "\"provenance\": \"\"}" +
                                                    "]}";



    private Thing thing;

    @Before
    public void setupUp() throws Exception {
        thing = new Thing();
        thing.thingURI = FT_THING_URI;
        thing.identifierValue = "";
        thing.prefLabel = PREF_LABEL;
        List<Thing> equivalence = new ArrayList<Thing>();
        Thing equivOne = new Thing();
        equivOne.thingURI = EQUIV_THING_ONE_URI;
        equivOne.prefLabel = PREF_LABEL + " One";
        equivOne.identifierValue = "1";
        equivalence.add(equivOne);
        Role role = new Role();
        role.thingURI = AGENT_URI;
        role.label = "Manager";
        Agent agent = new Agent();
        agent.thingURI = ROLE_URI;
        agent.label = "Jem Rayfield";
        AgentRole agentRole = new AgentRole();
        agentRole.thingURI = AGENT_ROLE_URI;
        agentRole.role = role;
        agentRole.hadAgent = agent;
        Provenance provenance = new Provenance();
        provenance.thingURI = PROVENANCE_URI;
        provenance.agentRole = agentRole;
        provenance.atTime = DateTime.parse("12-12-12T12:12:12").toString();
        equivOne.provenance = provenance;
        Thing equivTwo = new Thing();
        equivTwo.thingURI = EQUIV_THING_TWO_URI;
        equivTwo.prefLabel = PREF_LABEL + " Two";
        equivTwo.identifierValue = "2";
        equivalence.add(equivTwo);
        thing.equivalentThings = equivalence;
    }

    @Test
    public void testToString() {
        System.out.println(thing);
        assertThat(thing.toString()).isEqualTo(THING_AS_STRING);
    }

}
