package com.ft.whakataki.lambda.thing.model;


import java.io.Serializable;

public class AgentRole  implements Serializable {


    private static final long serialVersionUID = -6109395830641856327L;

    public String thingURI;
    public Agent hadAgent;
    public Role role;

    public String toString() {
        return "{ \"hadAgent\": \"" + hadAgent + "\", " +
                "role\": \"" + role + "\" }";
    }

}
