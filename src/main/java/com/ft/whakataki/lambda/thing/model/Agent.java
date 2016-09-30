package com.ft.whakataki.lambda.thing.model;

import java.io.Serializable;

public class Agent implements Serializable {


    private static final long serialVersionUID = 7500856805211691919L;

    public String thingURI;
    public String label;

    public String toString() {
        return (label!=null) ? label :"";
    }

}
