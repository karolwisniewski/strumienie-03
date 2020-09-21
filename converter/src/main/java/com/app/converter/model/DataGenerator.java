package com.app.converter.model;

import com.app.converter.generic.JsonConverter;

import java.util.List;

public class DataGenerator extends JsonConverter<List<Order>> {
    public DataGenerator(String jsonFilename) {
        super(jsonFilename);
    }

}
