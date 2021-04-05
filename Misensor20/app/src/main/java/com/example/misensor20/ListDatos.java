package com.example.misensor20;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.misensor20.API.API;
import com.example.misensor20.model.Apidata;
import com.example.misensor20.model.HumedadYTemperatura_model;
import com.example.misensor20.model.feeds;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListDatos implements Serializable {
    private List<feeds> Data = null;

    public ListDatos(List<feeds> data) {
        Data = data;
    }

    public List<feeds> getData() {
        return Data;
    }

    public void setData(List<feeds> data) {
        Data = data;
    }
}
