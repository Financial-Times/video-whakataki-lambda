package com.ft.whakataki.lambda.thing.service.geo;

import com.ft.whakataki.lambda.blazegraphrepo.BlazeGraphNameSpaceRepo;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.ld.JSONLDUtil;
import com.ft.whakataki.lambda.thing.exception.GeoIdNotFoundException;
import com.ft.whakataki.lambda.thing.exception.ThingNotFoundException;
import com.ft.whakataki.lambda.thing.model.geo.GNLocation;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.*;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class RepoGeoIdJSONService extends GeoJSONServiceBase implements GeoIdJSONService {

    private static final String GEO_SEARCH_BY_ID_SPARQL = loadFileAsString("/query/locationByIdConstruct.sparql");

    private final String GEONAMES_NAMESPACE = "http://www.geonames.org/ontology#";
    private final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private final URI RDF_TYPE = uri(RDF_NAMESPACE + "type");
    private final URI GEONAME_FEATURE = uri(GEONAMES_NAMESPACE + "Feature");


    private BlazeGraphNameSpaceRepo nameSpaceRepo;
    private Configuration configuration;

    private ValueFactory valueFactory = new ValueFactoryImpl();

    public RepoGeoIdJSONService(Configuration configuration) {
        this.configuration = configuration;
        nameSpaceRepo = new BlazeGraphNameSpaceRepo(configuration);
    }

    @Override
    public GNLocation getGeoById(String id) {
        String sparql = GEO_SEARCH_BY_ID_SPARQL.replace("{{geonamesId}}", id);
        Model geoModel = nameSpaceRepo.executeGraphQuery(sparql);

        if ((geoModel != null) && (geoModel.size() > 0)) {
            Set<Resource> geoNamesURIValues = geoModel.filter(null, RDF_TYPE, GEONAME_FEATURE).subjects();
            if (!geoNamesURIValues.isEmpty()) {
                return createGNLocation(geoNamesURIValues.iterator().next().stringValue(), geoModel);
            } else {
                throw new GeoIdNotFoundException("404 - Geonames Id Not Found" + id);
            }
        } else {
            throw new GeoIdNotFoundException("404 - Geonames Id Not Found" + id);
        }
    }

}
