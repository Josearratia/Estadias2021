package com.example.misensor20.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HumedadYTemperatura_model {

    @SerializedName("feeds")
    private List<feeds> feeds;

    public List<feeds> getfeeds() {
        return feeds;
    }

}
