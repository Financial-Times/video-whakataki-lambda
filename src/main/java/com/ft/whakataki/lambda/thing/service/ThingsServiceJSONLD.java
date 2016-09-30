package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.ld.JSONLDUtil;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;

import java.io.IOException;
import java.util.UUID;

import static java.util.Objects.requireNonNull;


public class ThingsServiceJSONLD implements ThingsJSONLDService, ThingJSONLDService {


    private final String THING_URI_PREFIX = "http://api.ft.com/things/";
    private final String THING = "http://www.ft.com/ontology/thing/Thing";
    private static final String THING_BY_ID = loadFileAsString("/query/searchByIdQuad.sparql");
    private static final String THING_BY_URI = loadFileAsString("/query/getThingByUUIDEquivalenceProvenanceQuad.sparql");
    private static final String SEARCH_BY_LABEL = loadFileAsString("/query/searchByLabel.sparql");


    private BlazeGraphNameSpaceRepo nameSpaceRepo;
    private Configuration configuration;


    public ThingsServiceJSONLD(Configuration configuration) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }


    public String getThingById(String id) {
        String sparql = THING_BY_ID.replace("{{thingIdentifierValue}}", id);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            String result = JSONLDUtil.convertThingModelToJsonLD(thingModel);
            result = result.replace("\n", "");
            result = result.replace("\\", "");
            return result;
        } else {
            return null;
        }
    }

    public String getThingByUUID(String uuid) {
        String thingURI = THING_URI_PREFIX + requireNonNull(uuid);
        String sparql = THING_BY_URI.replace("{{thingUri}}", thingURI);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            return JSONLDUtil.convertThingModelToJsonLD(thingModel);
        } else {
            return null;
        }
    }

    public String getThingByLabel(String label, String type) {
        if (type==null || type.equals(""))
            type = THING;
        String sparql = SEARCH_BY_LABEL.replace("{{labelSearch}}", label).replace("{{type}}", type);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            return JSONLDUtil.convertThingModelToJsonLD(thingModel);
        } else {
            return null;
        }
    }

    public BlazeGraphNameSpaceRepo getNameSpaceRepo() {
        return nameSpaceRepo;
    }

    public void setNameSpaceRepo(BlazeGraphNameSpaceRepo nameSpaceRepo) {
        this.nameSpaceRepo = nameSpaceRepo;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    private static String loadFileAsString(String filename) {
        try {
            return IOUtils.toString(ThingService.class.getResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
