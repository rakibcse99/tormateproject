package com.example.tourmate;


import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tourmate.databinding.FragmentNearByBinding;
import com.example.tourmate.nearby.NearbyResponseBody;
import com.example.tourmate.nearby.Result;
import com.example.tourmate.viewmodels.LocationViewModel;
import com.example.tourmate.viewmodels.NearbyViewModel;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    private static final String TAG = NearByFragment.class.getSimpleName();
    private GoogleMap googleMap;
    private LocationViewModel locationViewModel;
    private FragmentNearByBinding binding;
    private String type = "";
    private Location myLocation = null;
    private NearbyViewModel nearbyViewModel;
    private List<Geofence> geofenceList = new ArrayList<>();
    private GeofencingClient geofencingClient;


    public NearByFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationViewModel =
                ViewModelProviders.of(getActivity())
                        .get(LocationViewModel.class);
        nearbyViewModel =
                ViewModelProviders.of(getActivity())
                        .get(NearbyViewModel.class);
        geofencingClient = LocationServices.getGeofencingClient(getContext());

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_near_by, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.nearbymap);
        mapFragment.getMapAsync(this);

        binding.btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "restaurant";
                googleMap.clear();
                getNearbyPlace();
            }
        });

        binding.btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "hospital";
                googleMap.clear();
                getNearbyPlace();
            }
        });

        binding.btnAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "atm";
                googleMap.clear();
                getNearbyPlace();
            }
        });
    }

    private void getNearbyPlace() {

        String apiKey = getString(R.string.nearby_place_api);
        String endUrl = String.format("place/nearbysearch/json?location=%f,%f&radius=1000&type=%s&key=%s",
                myLocation.getLatitude(),
                myLocation.getLongitude(),
                type, apiKey);
        Log.e(TAG, myLocation.getLatitude() + "," + myLocation.getLongitude() + "type: " + type);
        nearbyViewModel.getNearbyResponse(endUrl)
                .observe(this, new Observer<NearbyResponseBody>() {
                    @Override
                    public void onChanged(NearbyResponseBody response) {
                        List<Result> results = response.getResults();
                        for (Result r : results) {
                            LatLng latLng =
                                    new LatLng(r.getGeometry().getLocation().getLat(),
                                            r.getGeometry().getLocation().getLng());
                            String name = r.getName();
                            addPlaceMarkersOnMap(latLng, name);
                        }
                    }
                });

    }

    private void addPlaceMarkersOnMap(LatLng latLng, String name) {
        googleMap.addMarker(new MarkerOptions()
                .position(latLng).title(name));
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Geofence");
        builder.setMessage("Enter a name for this place");
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter a name");
        builder.setView(editText);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();
                if (!name.isEmpty()) {
                    createGeofenceObject(latLng, name);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLongClickListener(this);
        locationViewModel.locationLD.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                myLocation = location;
                LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().title("My Position")
                        .position(myPos));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 12f));
            }
        });

    }


    private void createGeofenceObject(LatLng latLng, String name) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(name)
                .setCircularRegion(latLng.latitude, latLng.latitude, 100)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(6 * 60 * 60 * 1000)
                .build();
        geofenceList.add(geofence);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(),
                getPendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Your selected place has been added and will be monitored", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getActivity(), GeofenceingTrigerringService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getContext(), 11, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        /*PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 11, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/
        return pendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(geofenceList);
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        return builder.build();
    }
}
