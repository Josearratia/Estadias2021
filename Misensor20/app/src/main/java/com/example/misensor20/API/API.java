package com.example.misensor20.API;

import com.example.misensor20.model.HumedadYTemperatura_model;
import com.example.misensor20.model.Posts;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

   @GET("channels/{user_channels}/feeds.json?")
    Call<HumedadYTemperatura_model> getData(
             @Path("user_channels") String user_channels
            ,@Query("api_key") String user_api_key
            ,@Query("results") String cantidad);

    @GET("posts")
    Call<List<Posts>> getposts();
}

