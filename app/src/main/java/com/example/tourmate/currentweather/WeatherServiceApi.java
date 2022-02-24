package com.example.tourmate.currentweather;

import com.example.tourmate.forecastweather.ForecastWeatherResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServiceApi {

    @GET
    Call<CurrentWeatherResponseBody> getCurrentWeatherInfo(@Url String endUrl);

    @GET
    Call<ForecastWeatherResponseBody> getForecastWeatherInfo(@Url String endUrl);
}
