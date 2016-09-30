package com.ft.whakataki.lambda.thing.service;

import static java.util.Objects.requireNonNull;

import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.cache.LiveStaleCache;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.*;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class ThingService implements ThingsService, ThingServiceI {


    private static final Logger LOGGER = LoggerFactory.getLogger(ThingService.class);

    private final String PROCOL = "http://";
    private final String RESOURCE = "/things/";
    private final Pattern THING_UUID_REG_EX = Pattern.compile("http://.*/things/(.*)");

    private final String NAME_SPACE = "whakataki";
    private final String SERVICE_URL = "http://blazegraph-1791397694.eu-west-1.elb.amazonaws.com//blazegraph";
    private final String API_GW = "p8c0zf0s6d.execute-api.eu-west-1.amazonaws.com/dev";

    private final String THING_URI_PREFIX = "http://api.ft.com/things/";
    private final String THING = "http://www.ft.com/ontology/thing/Thing";
    private final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    private static final URI PREF_LABEL = new URIImpl("http://www.ft.com/ontology/thing/prefLabel");
    private static final URI IDENTIFIER_VALUE = new URIImpl("http://www.ft.com/ontology/thing/identifierValue");
    private static final URI EQUIVALENT_THING = new URIImpl("http://www.ft.com/ontology/thing/equivalent");
    private static final URI PROVENANCE = new URIImpl("http://www.ft.com/ontology/event/prov");
    private static final URI PROVENANCE_AT_TIME = new URIImpl("http://www.ft.com/ontology/event/atTime");
    private static final URI PROVENANCE_AGENT_ROLE = new URIImpl("http://www.ft.com/ontology/event/agentRole");
    private static final URI PROVENANCE_HAD_AGENT = new URIImpl("http://www.ft.com/ontology/agent/hadAgent");
    private static final URI PROVENANCE_ROLE = new URIImpl("http://www.ft.com/ontology/agent/role");
    private static final URI THING_LABEL = new URIImpl("http://www.ft.com/ontology/thing/label");

    private static final String THING_BY_URI = loadFileAsString("/query/getThingByUUIDEquivalenceProvenanceQuad.sparql");
    private static final String THING_BY_ID = loadFileAsString("/query/searchByIdQuad.sparql");
    private static final String SEARCH_BY_LABEL = loadFileAsString("/query/searchByLabel.sparql");


    private BlazeGraphNameSpaceRepo nameSpaceRepo;
    private Configuration configuration;

    private DateTimeFormatter xsdDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTimeFormatter xsdTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss:SSS");


    private ValueFactory valueFactory = new ValueFactoryImpl();

    private Boolean useCache = false;
    private LiveStaleCache<Thing> liveStaleCache;

    public ThingService() {
        configuration = new Configuration();
        configuration.setNamespace(NAME_SPACE);
        configuration.setServiceURL(SERVICE_URL);
        configuration.setApiGw(API_GW);
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }

    public ThingService(BlazeGraphNameSpaceRepo nameSpaceRepo) {
        this.nameSpaceRepo = nameSpaceRepo;
    }

    public ThingService(Configuration configuration) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }

    public ThingService(Configuration configuration, LiveStaleCache<Thing> liveStaleCache) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
        this.liveStaleCache = liveStaleCache;
    }



    public Thing getThingById(String id) {
        String sparql = THING_BY_ID.replace("{{thingIdentifierValue}}", id);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            Set<Resource> thingURIValues = thingModel.filter(null, IDENTIFIER_VALUE, literal(id, "en")).subjects();
            if (!thingURIValues.isEmpty()) {
                String thingURI = thingURIValues.iterator().next().stringValue();
                return createThing(thingURI, thingModel);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Things getThingByLabel(String label, String type) {
        Things result = new Things();
        if (type==null || type.equals(""))
            type = THING;
        String sparql = SEARCH_BY_LABEL.replace("{{labelSearch}}", label).replace("{{type}}", type);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            Set<Resource> thingURIValues = thingModel.filter(null, uri(RDF_TYPE), uri(type)).subjects();
            List<Thing> things = thingURIValues.stream().map( v -> createThing(v.stringValue(), thingModel) ).collect(toList());
            result.things = things;
        }
        return result;
    }

    public Thing getThingByUUID(String uuid) {
        String thingURI = THING_URI_PREFIX + requireNonNull(uuid);
        String sparql = THING_BY_URI.replace("{{thingUri}}", thingURI);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            return createThing(thingURI, thingModel);
        } else {
            throw new ThingNotFoundException("No thing found for [" + thingURI + "]", uuid);
        }
    }

    private Thing createThing(String thingURI, Model thingModel) {
        Thing thing = new Thing();
        thing.thingURI = subsHostResouce(thingURI);
        addPrefLabel(thing, thingURI, thingModel);
        addIdentifierValue(thing, thingURI, thingModel);
        addEquivalentThings(thing, thingURI, thingModel);
        addProvenance(thing, thingURI, thingModel);
        return thing;
    }

    public String subsHostResouce(String thingURI) {
        Matcher m = THING_UUID_REG_EX.matcher(thingURI);
        if (m.find()) {
            return PROCOL + getConfiguration().getApiGw() + RESOURCE + m.group(1);
        } else {
            return thingURI;
        }
    }

    private Thing addProvenance(Thing thing, String thingURI, Model thingModel) {
       thing.provenance = getProvenance(thingURI, thingModel);
       return thing;
    }

    private Provenance getProvenance(String uri, Model thingModel) {
        Set<Value> provValues = thingModel.filter(new URIImpl(uri), PROVENANCE, null).objects();
        if (provValues.size() > 1) {
            LOGGER.warn("{} has more than one provenance value", uri);
        }
        if (!provValues.isEmpty()) {
            Provenance provenance = new Provenance();
            URI provenanceURI = new URIImpl(provValues.iterator().next().stringValue());
            Set<Value> atTimeValues = thingModel.filter(provenanceURI, PROVENANCE_AT_TIME, null).objects();
            if (!atTimeValues.isEmpty()) {
                provenance.atTime = DateTime.parse(atTimeValues.iterator().next().stringValue()).toString();
            }
            Set<Value> agentRoleValues = thingModel.filter(provenanceURI, PROVENANCE_AGENT_ROLE, null).objects();
            if (!agentRoleValues.isEmpty()) {
                URI agentRoleURI = new URIImpl(agentRoleValues.iterator().next().stringValue());
                provenance.agentRole = getAgentRole(agentRoleURI, thingModel);
            }
            return provenance;
        }
        return null;
    }

    private AgentRole getAgentRole(URI agentRoleURI, Model thingModel) {
        AgentRole agentRole = new AgentRole();
        agentRole.thingURI = subsHostResouce(agentRoleURI.stringValue());
        Set<Value> agentValues = thingModel.filter(agentRoleURI, PROVENANCE_HAD_AGENT, null).objects();
        if (!agentValues.isEmpty()) {
            URI agentURI = new URIImpl(agentValues.iterator().next().stringValue());
            agentRole.hadAgent = getAgent(agentURI, thingModel);
        }
        Set<Value> roleValues = thingModel.filter(agentRoleURI, PROVENANCE_ROLE, null).objects();
        if (!roleValues.isEmpty()) {
            URI roleURI = new URIImpl(roleValues.iterator().next().stringValue());
            agentRole.role = getRole(roleURI, thingModel);
        }
        return agentRole;
    }

    private Agent getAgent(URI agentURI, Model thingModel) {
        Agent agent = new Agent();
        agent.thingURI = subsHostResouce(agentURI.stringValue());
        Set<Value> agentLabelValues = thingModel.filter(agentURI, THING_LABEL, null).objects();
        if (!agentLabelValues.isEmpty()) {
            String agentLabel = agentLabelValues.iterator().next().stringValue();
            agent.label = agentLabel;
        }
        return agent;
    }

    private Role getRole(URI roleURI, Model thingModel) {
        Role role = new Role();
        role.thingURI = subsHostResouce(roleURI.stringValue());
        Set<Value> roleLabelValues = thingModel.filter(roleURI, THING_LABEL, null).objects();
        if (!roleLabelValues.isEmpty()) {
            String roleLabel = roleLabelValues.iterator().next().stringValue();
            role.label = roleLabel;
        }
        return role;
    }

    private Thing addEquivalentThings(Thing thing, String thingURI, Model thingModel) {
        thing.equivalentThings = getEquivalentThings(thingURI, thingModel);
        return thing;
    }

    private List<Thing> getEquivalentThings(String uri, Model thingModel) {
        Set<Value> equivalentThings = thingModel.filter(new URIImpl(uri), EQUIVALENT_THING, null).objects();
        List<Thing> listOfThings = equivalentThings.stream().map( v -> getEquivalentThing(v, thingModel) ).collect(toList());
        return listOfThings;
    }

    private Thing getEquivalentThing(Value equivalentValue, Model thingModel) {
        Thing thing = new Thing();
        String thingURI = equivalentValue.stringValue();
        thing.thingURI = subsHostResouce(thingURI);
        addPrefLabel(thing, thingURI, thingModel);
        addIdentifierValue(thing, thingURI, thingModel);
        addEquivalentThings(thing, thingURI, thingModel);
        addProvenance(thing, thingURI, thingModel);
        return thing;
    }

    private Thing addIdentifierValue(Thing thing, String thingURI, Model model) {
        thing.identifierValue = getIdentifierValue(thingURI, model);
        return thing;
    }

    private String getIdentifierValue(String uri, Model model) {

        Set<Value> identifierValues = model.filter(new URIImpl(uri), IDENTIFIER_VALUE, null).objects();
        if (identifierValues.size() > 1) {
            LOGGER.warn("{} has more than one identifierValue", uri);
        }
        if (!identifierValues.isEmpty()) return identifierValues.iterator().next().stringValue();
        return null;
    }


    private Thing addPrefLabel(Thing thing, String thingURI, Model model) {
        thing.prefLabel = getPrefLabel(thingURI, model);
        return thing;
    }

    private String getPrefLabel(String uri, Model model) {
        Set<Value> prefLabels = model.filter(new URIImpl(uri), PREF_LABEL, null).objects();
        if (prefLabels.size() > 1) {
            LOGGER.warn("{} has more than one prefLabel", uri);
        }
        if (!prefLabels.isEmpty()) return prefLabels.iterator().next().stringValue();
        return null;
    }

    private static String loadFileAsString(String filename) {
        try {
            return IOUtils.toString(ThingService.class.getResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Configuration getConfiguration(){
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    private Literal literal(String literal, String languageCode) {
        return valueFactory.createLiteral(literal, languageCode);
    }

    private URI uri(String uri) {
        return valueFactory.createURI(uri);
    }

    private Literal literal(String literal) {
        return valueFactory.createLiteral(literal);
    }

    public Boolean getUseCache() {
        return useCache;
    }

    public void setUseCache(Boolean useCache) {
        this.useCache = useCache;
    }

    public LiveStaleCache<Thing> getLiveStaleCache() {
        return liveStaleCache;
    }

    public void setLiveStaleCache(LiveStaleCache<Thing> liveStaleCache) {
        this.liveStaleCache = liveStaleCache;
    }


}
