package com.ft.whakataki.lambda.thing.model;


import java.io.Serializable;

public class Role implements Serializable{


    private static final long serialVersionUID = -8546439954538304875L;

    public String thingURI;
    public String label;

    public String toString() {
        return (label!=null) ? label : "";
    }

}
