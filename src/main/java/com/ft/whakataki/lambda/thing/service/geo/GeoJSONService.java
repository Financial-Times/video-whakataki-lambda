package com.ft.whakataki.lambda.thing.service.geo;

import com.ft.whakataki.lambda.thing.model.geo.GNLocation;
import com.ft.whakataki.lambda.thing.model.geo.GNLocations;

public interface GeoJSONService {

    public GNLocations getGeoByLabel(String searchString);

}
