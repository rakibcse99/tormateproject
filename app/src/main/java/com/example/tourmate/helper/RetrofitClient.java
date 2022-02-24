package com.example.tourmate.helper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://api.openweathermap.org/";
    private static final String NEARBY_BASE_URL = "https://maps.googleapis.com/maps/api/";
    public static Retrofit getClientForWeather(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getClientForNearby(){
        return new Retrofit.Builder()
                .baseUrl(NEARBY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
