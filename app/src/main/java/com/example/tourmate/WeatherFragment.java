package com.example.tourmate;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tourmate.adapter.ForecastAdapter;
import com.example.tourmate.currentweather.CurrentWeatherResponseBody;
import com.example.tourmate.currentweather.WeatherServiceApi;
import com.example.tourmate.databinding.FragmentWeatherBinding;
import com.example.tourmate.forecastweather.ForecastList;
import com.example.tourmate.forecastweather.ForecastWeatherResponseBody;
import com.example.tourmate.helper.EventUtils;
import com.example.tourmate.helper.RetrofitClient;
import com.example.tourmate.viewmodels.LocationViewModel;
import com.example.tourmate.viewmodels.WeatherViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = WeatherFragment.class.getSimpleName();

    private FragmentWeatherBinding binding;
    private LocationViewModel locationViewModel;
    private WeatherViewModel weatherViewModel;
    private Location currentlocation = null;
    private PlacesClient placesClient;
    private LinearLayout bottom_sheet;
    private TextView bottomSheetHeaderTitle;
    private BottomSheetBehavior bottomSheetBehavior;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private RecyclerView forecastRV;
    private String units = EventUtils.WeatherUtils.UNIT_CELCIUS;
    private String symbol = EventUtils.WeatherUtils.UNIT_CELCIUS_SYMBOL;

    private String lat;
    private String lon;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.weather_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.weather_item_search).getActionView();
        searchView.setQueryHint("search any city");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    getLatLngForQuery(query);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Couldn't find your location", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getLatLngForQuery(String query) throws IOException {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses = geocoder.getFromLocationName(query, 1);
        if (addresses != null && addresses.size() > 0) {
            double lat = addresses.get(0).getLatitude();
            double lng = addresses.get(0).getLongitude();
            currentlocation.setLatitude(lat);
            currentlocation.setLongitude(lng);
            getCurrentWeather(currentlocation);
            getForecastWeather(currentlocation);
        } else {
            Toast.makeText(getActivity(), "Please provide a valid place name", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem celItem = menu.findItem(R.id.weather_item_celcius);
        MenuItem fhtItem = menu.findItem(R.id.weather_item_fahrenheit);
        if (units.equals(EventUtils.WeatherUtils.UNIT_CELCIUS)) {
            celItem.setVisible(false);
            binding.celcius.setEnabled(false);
            fhtItem.setVisible(true);
        } else if (units.equals(EventUtils.WeatherUtils.UNIT_FAHRENHEIT)) {
            celItem.setVisible(true);
            fhtItem.setVisible(false);
            binding.fahrenheit.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.weather_item_celcius:
                units = EventUtils.WeatherUtils.UNIT_CELCIUS;
                symbol = EventUtils.WeatherUtils.UNIT_CELCIUS_SYMBOL;
                getCurrentWeather(currentlocation);
                getForecastWeather(currentlocation);

                break;
            case R.id.weather_item_fahrenheit:
                units = EventUtils.WeatherUtils.UNIT_FAHRENHEIT;
                symbol = EventUtils.WeatherUtils.UNIT_FAHRENHEIT_SYMBOL;
                getCurrentWeather(currentlocation);
                getForecastWeather(currentlocation);
                break;

            case R.id.weather_item_search:
                showPlaceSearchDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPlaceSearchDialog() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng latLng = place.getLatLng();
            Location searchLocation = new Location(LocationManager.GPS_PROVIDER);
            searchLocation.setLatitude(latLng.latitude);
            searchLocation.setLongitude(latLng.longitude);
            currentlocation = searchLocation;
            getCurrentWeather(searchLocation);
            getForecastWeather(searchLocation);
        }
    }


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Places.initialize(getActivity(), getString(R.string.places_api));
        placesClient = Places.createClient(getActivity());
        binding = FragmentWeatherBinding.inflate(LayoutInflater.from(getActivity()));
        locationViewModel =
                ViewModelProviders.of(getActivity())
                        .get(LocationViewModel.class);
        weatherViewModel =
                ViewModelProviders.of(getActivity())
                        .get(WeatherViewModel.class);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottom_sheet = view.findViewById(R.id.forecastListBottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetHeaderTitle = bottom_sheet.findViewById(R.id.titleHeaderTV);
        forecastRV = bottom_sheet.findViewById(R.id.forecastRV);


        locationViewModel.locationLD.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.e("LatLon",location.getLatitude()+", "+location.getLongitude());
                //binding.latLngTV.setText(location.getLatitude()+", "+location.getLongitude());
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
                currentlocation = location;

                getCurrentWeather(currentlocation);
                getForecastWeather(currentlocation);


//
//                try {
//                    convertLatLngToStreetAddress();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


            }
        });


        bottomSheetHeaderTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        //change weather with button
        binding.celcius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                units = EventUtils.WeatherUtils.UNIT_CELCIUS;
                symbol = EventUtils.WeatherUtils.UNIT_CELCIUS_SYMBOL;
                getCurrentWeather(currentlocation);
                getForecastWeather(currentlocation);
                binding.celcius.setEnabled(false);
                binding.fahrenheit.setEnabled(true);
            }
        });

        binding.fahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                units = EventUtils.WeatherUtils.UNIT_FAHRENHEIT;
                symbol = EventUtils.WeatherUtils.UNIT_FAHRENHEIT_SYMBOL;
                getCurrentWeather(currentlocation);
                getForecastWeather(currentlocation);
                binding.fahrenheit.setEnabled(false);
                binding.celcius.setEnabled(true);
            }
        });


    }


    private void getForecastWeather(Location location) {
        weatherViewModel.getForecastData(location, getString(R.string.weather_api_key), units)
                .observe(this, new Observer<ForecastWeatherResponseBody>() {
                    @Override
                    public void onChanged(ForecastWeatherResponseBody response) {
                        List<ForecastList> forecastLists = response.getForecastList();
                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        forecastRV.setLayoutManager(llm);
                        ForecastAdapter adapter = new ForecastAdapter(getActivity(), forecastLists);
                        forecastRV.setAdapter(adapter);
                    }
                });
    }

    private void getCurrentWeather(Location location) {
        String api = getString(R.string.weather_api_key);
        weatherViewModel.getCurrentData(location, api, units)
                .observe(this, new Observer<CurrentWeatherResponseBody>() {
                    @Override
                    public void onChanged(CurrentWeatherResponseBody response) {

                        String iconUrl = EventUtils.WeatherUtils.ICON_URL_PREFIX + response.getWeather().get(0).getIcon() + ".png";
                        Picasso.get().load(iconUrl).into(binding.wIcon);
                        binding.wDgree.setText(String.valueOf(Math.round(response.getMain().getTemp())) + symbol );
                        binding.wCondition.setText(response.getWeather().get(0).getMain());
                        binding.wdateTime.setText(EventUtils.getFormattedDate(response.getDt()));
                        binding.wWind.setText(String.valueOf(response.getWind().getSpeed() + " m/s / " + response.getWind().getDeg() + symbol));
                        binding.wCloudness.setText(response.getWeather().get(0).getDescription());
                        binding.wPressure.setText(String.valueOf(response.getMain().getPressure())+" hpa");
                        binding.wHumidity.setText(String.valueOf(response.getMain().getHumidity())+" %");
                        binding.wSunrise.setText(EventUtils.getFormattedTime(response.getSys().getSunrise()));
                        binding.wSunset.setText(EventUtils.getFormattedTime(response.getSys().getSunset()));

                        try {
                            convertLatLngToStreetAddress();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //Custom Method
    private void convertLatLngToStreetAddress() throws IOException {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(lat),
                Double.parseDouble(lon), 1);
        String addressR = addressList.get(0).getAddressLine(0);
        binding.wAddress.setText(addressR);

    }



}