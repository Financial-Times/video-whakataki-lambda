package com.ft.whakataki.lambda.repo.geo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.geo.GNLocations;
import com.ft.whakataki.lambda.thing.model.geo.GeoRequest;
import com.ft.whakataki.lambda.thing.service.geo.GeoJSONLdService;
import com.ft.whakataki.lambda.thing.service.geo.GeoJSONService;

public class GeoJSONRepository implements Repository {

    GeoJSONService geoJSONService;

    public GeoJSONRepository(GeoJSONService geoJSONService) {
        this.geoJSONService = geoJSONService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        GNLocations gnLocations = geoJSONService.getGeoByLabel(((GeoRequest)request).getSearchString());
        return  ResponseBuilder.create(gnLocations, GNLocations.class.getTypeName()).build();
    }

}
