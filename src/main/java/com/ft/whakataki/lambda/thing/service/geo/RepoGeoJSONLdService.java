package com.ft.whakataki.lambda.thing.service.geo;


import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.ld.JSONLDUtil;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;

import java.io.IOException;

public class RepoGeoJSONLdService implements GeoJSONLdService {

    private static final String GEO_SEARCH_BY_LABEL_SPARQL = loadFileAsString("/query/locationSearchConstruct.sparql");

    private BlazeGraphNameSpaceRepo nameSpaceRepo;
    private Configuration configuration;

    public RepoGeoJSONLdService(Configuration configuration) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }

    @Override
    public String getGeoByLabel(String searchString) {
        String sparql = GEO_SEARCH_BY_LABEL_SPARQL.replace("{{searchString}}", searchString);
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
