package com.ft.whakataki.lambda.thing.service;

import com.ft.whakataki.lambda.thing.model.Thing;

import java.util.UUID;

public interface ThingServiceI {

    public Thing getThingByUUID(String uuid);
}
