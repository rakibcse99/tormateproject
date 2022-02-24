package com.example.tourmate.repos;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tourmate.pojos.LocationPicPojo;
import com.example.tourmate.pojos.MomentPojo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class LocationPicRepository {
    private FirebaseUser firebaseUser;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference locationPicRef;
    public MutableLiveData<List<LocationPicPojo>> locationPicLD = new MutableLiveData<>();

    public LocationPicRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(firebaseUser.getUid());
        locationPicRef = userRef.child("LocationPics");
        locationPicRef.keepSynced(true);
    }

    public void addNewLocationPic(LocationPicPojo locationPics){
        String locationPicId = locationPicRef.push().getKey();
        locationPics.setLocationPicId(locationPicId);
        locationPicRef.child(locationPics.getEventId())
                .child(locationPicId)
                .setValue(locationPics);
    }


    public MutableLiveData<List<LocationPicPojo>> getAllLocationPics(String eventId){
        locationPicRef.child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<LocationPicPojo> locationPics = new ArrayList<>();
                        for (DataSnapshot d : dataSnapshot.getChildren()){
                            locationPics.add(d.getValue(LocationPicPojo.class));
                        }
                        locationPicLD.postValue(locationPics);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return locationPicLD;
    }

    public void deleteImagefromDB(LocationPicPojo locationPicPojo) {
        String momentID = locationPicPojo.getLocationPicId();
        locationPicRef.child(locationPicPojo.getEventId())
                .child(momentID)
                .removeValue();

        //Delete also firebaseStrogae

        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(locationPicPojo.getDownloadUrl());



        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                /// Toast.makeText(, "", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}
