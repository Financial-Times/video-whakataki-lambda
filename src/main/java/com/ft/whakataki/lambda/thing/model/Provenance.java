package com.ft.whakataki.lambda.thing.model;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Provenance implements Serializable {


    private static final long serialVersionUID = -3915712436348496296L;

    public String thingURI;
    public String atTime;
    public AgentRole agentRole = new AgentRole();

    public String toString() {
        return "{ \"atTime\": \"" + atTime + "\", " +
                "agentLabel\": \"" + agentRole.hadAgent + "\", " +
                "roleLabel\": \"" + agentRole.role + "\" }";

    }

}
