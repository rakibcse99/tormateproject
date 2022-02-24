package com.example.tourmate.viewmodels;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.currentweather.CurrentWeatherResponseBody;
import com.example.tourmate.forecastweather.ForecastWeatherResponseBody;
import com.example.tourmate.repos.WeatherRepository;


public class WeatherViewModel extends ViewModel {
    private static final String TAG = WeatherViewModel.class.getSimpleName();
    private WeatherRepository repository;

    public WeatherViewModel(){
        repository = new WeatherRepository();
    }

    public MutableLiveData<CurrentWeatherResponseBody> getCurrentData(Location location, String api, String units){
        //Log.e(TAG, "getCurrentData called");
        return repository.getCurrentWeather(location, api, units);
    }

    public MutableLiveData<ForecastWeatherResponseBody> getForecastData(Location location, String api, String units){
        return repository.getForecastWeather(location, api, units);
    }
}
