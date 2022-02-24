package com.example.tourmate.nearby;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NearbyService {
    @GET
    Call<NearbyResponseBody> getNearbyPlaces(@Url String endUrl);
}
