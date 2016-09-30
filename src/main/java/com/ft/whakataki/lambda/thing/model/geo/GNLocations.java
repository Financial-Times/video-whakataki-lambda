package com.ft.whakataki.lambda.thing.model.geo;

import com.ft.whakataki.lambda.thing.model.Thing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GNLocations implements Serializable {

    private static final long serialVersionUID = 3800756467199756586L;

    public List<GNLocation> geonamesLocations = new ArrayList<>();

}
