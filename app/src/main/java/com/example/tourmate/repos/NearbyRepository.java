package com.example.tourmate.repos;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import com.example.tourmate.helper.RetrofitClient;
import com.example.tourmate.nearby.NearbyResponseBody;
import com.example.tourmate.nearby.NearbyService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyRepository {
    private static final String TAG = NearbyRepository.class.getSimpleName();
    public MutableLiveData<NearbyResponseBody> nearbyLD = new MutableLiveData<>();

    public MutableLiveData<NearbyResponseBody> getNearbyPlaces(String endUrl){
        NearbyService service = RetrofitClient
                .getClientForNearby()
                .create(NearbyService.class);
        service.getNearbyPlaces(endUrl)
                .enqueue(new Callback<NearbyResponseBody>() {
                    @Override
                    public void onResponse(Call<NearbyResponseBody> call, Response<NearbyResponseBody> response) {
                        if (response.isSuccessful()){
                            nearbyLD.postValue(response.body());
                            Log.e(TAG, "onResponse: "+response.body().getStatus());
                        }

                    }

                    @Override
                    public void onFailure(Call<NearbyResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
                    }
                });

        return nearbyLD;
    }
}
