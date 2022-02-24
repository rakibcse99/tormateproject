package com.example.tourmate.repos;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import com.example.tourmate.currentweather.CurrentWeatherResponseBody;
import com.example.tourmate.currentweather.WeatherServiceApi;
import com.example.tourmate.forecastweather.ForecastWeatherResponseBody;
import com.example.tourmate.helper.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherRepository {
    private static final String TAG = WeatherRepository.class.getSimpleName();
    private WeatherServiceApi serviceApi;
    public MutableLiveData<ForecastWeatherResponseBody> forecastLD = new MutableLiveData<>();
    public MutableLiveData<CurrentWeatherResponseBody> currentLD = new MutableLiveData<>();
    public WeatherRepository(){

        serviceApi = RetrofitClient.getClientForWeather().create(WeatherServiceApi.class);
    }

    public MutableLiveData<CurrentWeatherResponseBody> getCurrentWeather(Location location, String apiKey, String units){

        String endUrl = String.format("data/2.5/weather?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(),
                location.getLongitude(),
                units, apiKey);
        serviceApi.getCurrentWeatherInfo(endUrl)
                .enqueue(new Callback<CurrentWeatherResponseBody>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponseBody> call, Response<CurrentWeatherResponseBody> response) {
                        if (response.code() == 200){
                            CurrentWeatherResponseBody responseBody =
                                    response.body();
                            Log.e(TAG, "onResponse: "+responseBody.getMain().getTemp());
                            currentLD.postValue(responseBody);

                            //Log.e(TAG, "ld: "+currentLD.getValue().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
                    }
                });
        return currentLD;
    }

    public MutableLiveData<ForecastWeatherResponseBody> getForecastWeather(
            Location location, String apiKey, String units
    ){

        String endUrl = String.format("data/2.5/forecast/daily?lat=%f&lon=%f&units=%s&cnt=16&appid=%s",
                location.getLatitude(),
                location.getLongitude(),
                units, apiKey);
        serviceApi.getForecastWeatherInfo(endUrl)
                .enqueue(new Callback<ForecastWeatherResponseBody>() {
                    @Override
                    public void onResponse(Call<ForecastWeatherResponseBody> call, Response<ForecastWeatherResponseBody> response) {
                        if (response.isSuccessful()){
                            ForecastWeatherResponseBody responseBody =
                                    response.body();
                            forecastLD.postValue(responseBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastWeatherResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
                    }
                });
        return forecastLD;
    }
}
