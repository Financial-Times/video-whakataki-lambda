package com.ft.whakataki.lambda.repo.geo;

import com.ft.whakataki.lambda.common.Repository;
import com.ft.whakataki.lambda.common.Request;
import com.ft.whakataki.lambda.common.Response;
import com.ft.whakataki.lambda.common.ResponseBuilder;
import com.ft.whakataki.lambda.thing.model.geo.GeoRequest;
import com.ft.whakataki.lambda.thing.service.geo.GeoJSONLdService;

public class GeoJSONLDRepository implements Repository {

    GeoJSONLdService geoJSONLdService;

    public GeoJSONLDRepository(GeoJSONLdService geoJSONLdService) {
        this.geoJSONLdService = geoJSONLdService;
    }

    @Override
    public Response retreiveResponse(Request request) {
        String jsonLd = geoJSONLdService.getGeoByLabel(((GeoRequest)request).getSearchString());
        return  ResponseBuilder.create(jsonLd, String.class.getTypeName()).build();
    }

}
