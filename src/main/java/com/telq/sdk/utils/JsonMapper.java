package com.telq.sdk.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class JsonMapper {

    private static JsonMapper instance = null;

    private GsonBuilder gsonBuilder;
    private Gson gson;

    private JsonMapper() {
    }

    public static JsonMapper getInstance() {
        if (instance == null) {
            instance = new JsonMapper();
            instance.initialise();
        }
        return instance;
    }

    private void initialise() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        /*
        GSON by default is unable to parse Instant that it receives. So we have to add the mapper ourselves.
         */
        gson = gsonBuilder.registerTypeAdapter(
                Instant.class,
                (JsonDeserializer<Instant>) (jsonElement, type, jsonDeserializationContext) -> Instant.parse(jsonElement.getAsString())).create();
    }

    public Gson getMapper() {
        return gson;
    }


}
