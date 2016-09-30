package com.ft.whakataki.lambda.thing.service.geo;

import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.ld.JSONLDUtil;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;

import java.io.IOException;

public class RepoGeoIdJSONLdService implements GeoIdJSONLDService{

    private static final String GEO_SEARCH_BY_ID_SPARQL = loadFileAsString("/query/locationByIdConstruct.sparql");

    private BlazeGraphNameSpaceRepo nameSpaceRepo;
    private Configuration configuration;

    public RepoGeoIdJSONLdService(Configuration configuration) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }

    @Override
    public String getGeoById(String id) {
        String sparql = GEO_SEARCH_BY_ID_SPARQL.replace("{{geonamesId}}", id);
        Model thingModel = nameSpaceRepo.executeGraphQuery(sparql);
        if ((thingModel != null) && (thingModel.size() > 0)) {
            return JSONLDUtil.convertThingModelToJsonLD(thingModel);
        } else {
            return null;
        }
    }

    private static String loadFileAsString(String filename) {
        try {
            return IOUtils.toString(RepoGeoJSONLdService.class.getResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
