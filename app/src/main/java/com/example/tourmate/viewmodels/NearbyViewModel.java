package com.example.tourmate.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.nearby.NearbyResponseBody;
import com.example.tourmate.repos.NearbyRepository;


public class NearbyViewModel extends ViewModel {
    private NearbyRepository repository;

    public NearbyViewModel(){
        repository = new NearbyRepository();
    }

    public MutableLiveData<NearbyResponseBody> getNearbyResponse(String endUrl){
        return repository.getNearbyPlaces(endUrl);
    }
}
