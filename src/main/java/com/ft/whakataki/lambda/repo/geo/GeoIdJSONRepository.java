package com.ft.whakataki.lambda.repo.geo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.geo.GNLocation;
import com.ft.whakataki.lambda.thing.model.geo.GeoIdRequest;
import com.ft.whakataki.lambda.thing.service.geo.GeoIdJSONService;

public class GeoIdJSONRepository implements Repository {

    GeoIdJSONService geoIdJSONService;

    public GeoIdJSONRepository(GeoIdJSONService geoIdJSONService) {
        this.geoIdJSONService = geoIdJSONService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        GNLocation gnLocation = geoIdJSONService.getGeoById(((GeoIdRequest) request).getId());
        return ResponseBuilder.create(gnLocation, String.class.getTypeName()).build();
    }

}