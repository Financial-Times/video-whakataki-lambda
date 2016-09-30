package com.ft.whakataki.lambda.thing.model;

import com.ft.whakataki.lambda.common.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Thing implements Serializable {


    private static final long serialVersionUID = -6116525349947099548L;

    public String thingURI;
    public String prefLabel;
    public String identifierValue;
    public List<Thing> equivalentThings = new ArrayList<>();
    public Provenance provenance;


    public String toString() {
        String result = "{ \"thingURI\": \"" + thingURI + "\", " +
                  "\"prefLabel\": \"" + prefLabel + "\", " +
                  "\"identifierValue\": \"" + identifierValue + "\", ";

        if (provenance!=null)
            result = result + "\"provenance\": " + provenance +  "";
        else
            result = result + "\"provenance\": \"\"";
        if (equivalentThings.size()>0) {
            String equivalences = equivalentThings.stream().map( s -> s ).collect(Collectors.toList()).toString();
            equivalences = equivalences.replace("[","").replace("]","");
            result = result  + ", \"equivalent\": [ " + equivalences + "]}";
        } else {
            result = result + "}";
        }
        return result;
    }

}
