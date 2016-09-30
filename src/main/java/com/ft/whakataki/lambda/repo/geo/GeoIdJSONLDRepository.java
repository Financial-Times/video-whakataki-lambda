package com.ft.whakataki.lambda.repo.geo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.geo.GeoIdRequest;
import com.ft.whakataki.lambda.thing.service.geo.GeoIdJSONLDService;

public class GeoIdJSONLDRepository implements Repository {

    GeoIdJSONLDService geoIdJSONLdService;

    public GeoIdJSONLDRepository(GeoIdJSONLDService geoIdJSONLdService) {
        this.geoIdJSONLdService = geoIdJSONLdService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        String jsonLd = geoIdJSONLdService.getGeoById(((GeoIdRequest)request).getId());
        return  ResponseBuilder.create(jsonLd, String.class.getTypeName()).build();
    }

}
