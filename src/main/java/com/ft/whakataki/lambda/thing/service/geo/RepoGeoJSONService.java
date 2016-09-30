package com.ft.whakataki.lambda.thing.service.geo;

import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.thing.model.Provenance;
import com.ft.whakataki.lambda.thing.model.geo.GNLocation;
import com.ft.whakataki.lambda.thing.model.geo.GNLocations;

import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;


import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RepoGeoJSONService extends GeoJSONServiceBase implements GeoJSONService  {

    private final String GEONAMES_NAMESPACE = "http://www.geonames.org/ontology#";
    private final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private final URI RDF_TYPE = uri(RDF_NAMESPACE + "type");
    private final URI GEONAME_FEATURE = uri(GEONAMES_NAMESPACE + "Feature");


    private static final String GEO_SEARCH_BY_LABEL_SPARQL = loadFileAsString("/query/locationSearchConstruct.sparql");

    private BlazeGraphNameSpaceRepo nameSpaceRepo;
    private Configuration configuration;

    private ValueFactory valueFactory = new ValueFactoryImpl();

    public RepoGeoJSONService(Configuration configuration) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }

    @Override
    public GNLocations getGeoByLabel(String searchString) {
        GNLocations result = new GNLocations();
        result.geonamesLocations = new ArrayList<GNLocation>();

        String sparql = GEO_SEARCH_BY_LABEL_SPARQL.replace("{{searchString}}", searchString);
        Model geoModel = nameSpaceRepo.executeGraphQuery(sparql);

        if ((geoModel != null) && (geoModel.size() > 0)) {
            Set<Resource> geoNamesURIValues = geoModel.filter(null, RDF_TYPE, GEONAME_FEATURE).subjects();
            List<GNLocation> geonameLocations = geoNamesURIValues.stream().map(v -> createGNLocation(v.stringValue(), geoModel) ).collect(toList());
            result.geonamesLocations = geonameLocations;
        }
        return result;
    }

}
