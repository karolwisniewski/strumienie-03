package com.app.converter.model;

import com.app.converter.generic.JsonConverter;

import java.util.List;

public class CustomerConverter extends JsonConverter <List<Customer>>{
    public CustomerConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
