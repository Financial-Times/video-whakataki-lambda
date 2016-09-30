package com.ft.whakataki.lambda.thing.service.geo;

import com.ft.whakataki.lambda.thing.model.geo.GNLocation;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.io.IOException;
import java.util.Set;

public class GeoJSONServiceBase {

    private final String GEONAMES_NAMESPACE = "http://www.geonames.org/ontology#";
    private final String GEO_NAMESPACE = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    private final String SCORE_NAMESPACE = "http://www.ft.com/ontology/score/";
    private final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
    private final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private final URI RDF_TYPE = uri(RDF_NAMESPACE + "type");
    private final URI GEONAME_FEATURE = uri(GEONAMES_NAMESPACE + "Feature");
    private final URI GEONAME_NAME = uri(GEONAMES_NAMESPACE + "name");
    private final URI COUNTRY_CODE = uri(GEONAMES_NAMESPACE + "countryCode");
    private final URI RANK = uri(SCORE_NAMESPACE + "rank");
    private final URI RELEVANCE = uri(SCORE_NAMESPACE + "relevance");
    private final URI FEATURE_CLASS = uri(GEONAMES_NAMESPACE + "featureClass");
    private final URI FEATURE_CODE = uri(GEONAMES_NAMESPACE + "featureCode");
    private final URI WIKIPEDIA_ARTICLE = uri(GEONAMES_NAMESPACE + "wikipediaArticle");
    private final URI LOCATION_MAP = uri(GEONAMES_NAMESPACE + "locationMap");
    private final URI SEE_ALSO = uri(RDFS_NAMESPACE + "seeAlso");
    private final URI LATITUDE = uri(GEO_NAMESPACE + "lat");
    private final URI LONGITUDE = uri(GEO_NAMESPACE + "long");
    private final URI POPULATION = uri(GEONAMES_NAMESPACE + "population");

    private ValueFactory valueFactory = new ValueFactoryImpl();

    protected GNLocation createGNLocation(String geonamesURI, Model geonamesModel) {
        GNLocation gnLocation = new GNLocation();
        gnLocation.setGeonamesUri(geonamesURI);
        Set<Value> typeValues = geonamesModel.filter(new URIImpl(geonamesURI), RDF_TYPE, null).objects();
        if (!typeValues.isEmpty()) {
            gnLocation.setType(typeValues.iterator().next().stringValue());
        }
        Set<Value> nameValues = geonamesModel.filter(new URIImpl(geonamesURI), GEONAME_NAME, null).objects();
        if (!nameValues.isEmpty()) {
            gnLocation.setName(nameValues.iterator().next().stringValue());
        }
        Set<Value> countryCodeValues = geonamesModel.filter(new URIImpl(geonamesURI), COUNTRY_CODE, null).objects();
        if (!countryCodeValues.isEmpty()) {
            gnLocation.setCountryCode(countryCodeValues.iterator().next().stringValue());
        }
        Set<Value> rankValues = geonamesModel.filter(new URIImpl(geonamesURI), RANK, null).objects();
        if (!rankValues.isEmpty()) {
            gnLocation.setRank(Integer.parseInt(rankValues.iterator().next().stringValue()));
        }
        Set<Value> relevanceValues = geonamesModel.filter(new URIImpl(geonamesURI), RELEVANCE, null).objects();
        if (!relevanceValues.isEmpty()) {
            gnLocation.setRelevance(Double.parseDouble(relevanceValues.iterator().next().stringValue()));
        }
        Set<Value> featureClassValues = geonamesModel.filter(new URIImpl(geonamesURI), FEATURE_CLASS, null).objects();
        if (!featureClassValues.isEmpty()) {
            gnLocation.setFeatureClass(featureClassValues.iterator().next().stringValue());
        }
        Set<Value> featureCodeValues = geonamesModel.filter(new URIImpl(geonamesURI), FEATURE_CODE, null).objects();
        if (!featureCodeValues.isEmpty()) {
            gnLocation.setFeatureCode(featureCodeValues.iterator().next().stringValue());
        }
        Set<Value> wikipediaArticleValues = geonamesModel.filter(new URIImpl(geonamesURI), WIKIPEDIA_ARTICLE, null).objects();
        if (!wikipediaArticleValues.isEmpty()) {
            gnLocation.setWikipediaArticle(wikipediaArticleValues.iterator().next().stringValue());
        }
        Set<Value> locationMapValues = geonamesModel.filter(new URIImpl(geonamesURI), LOCATION_MAP, null).objects();
        if (!locationMapValues.isEmpty()) {
            gnLocation.setLocationMap(locationMapValues.iterator().next().stringValue());
        }
        Set<Value> seeAlsoValues = geonamesModel.filter(new URIImpl(geonamesURI), SEE_ALSO, null).objects();
        if (!seeAlsoValues.isEmpty()) {
            gnLocation.setSeeAlso(seeAlsoValues.iterator().next().stringValue());
        }
        Set<Value> latitudeValues = geonamesModel.filter(new URIImpl(geonamesURI), LATITUDE, null).objects();
        if (!latitudeValues.isEmpty()) {
            gnLocation.setLatitude(latitudeValues.iterator().next().stringValue());
        }
        Set<Value> longitudeValues = geonamesModel.filter(new URIImpl(geonamesURI), LONGITUDE, null).objects();
        if (!longitudeValues.isEmpty()) {
            gnLocation.setLongitude(longitudeValues.iterator().next().stringValue());
        }
        Set<Value> populationValues = geonamesModel.filter(new URIImpl(geonamesURI), POPULATION, null).objects();
        if (!populationValues.isEmpty()) {
            gnLocation.setPopulation(Integer.parseInt(populationValues.iterator().next().stringValue()));
        }
        return gnLocation;
    }

    protected static String loadFileAsString(String filename) {
        try {
            return IOUtils.toString(RepoGeoJSONService.class.getResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Literal literal(String literal, String languageCode) {
        return valueFactory.createLiteral(literal, languageCode);
    }

    protected URI uri(String uri) {
        if (valueFactory == null)
            valueFactory = new ValueFactoryImpl();
        return valueFactory.createURI(uri);
    }

    private Literal literal(String literal) {
        return valueFactory.createLiteral(literal);
    }

}
