package com.ft.whakataki.lambda.thing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Things implements Serializable {

    private static final long serialVersionUID = 2864802369177443844L;

    public List<Thing> things = new ArrayList<>();
}
