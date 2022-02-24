package com.example.tourmate.repos;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.pojos.TourMateEventPojo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventDBRepository {
    private FirebaseUser firebaseUser;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;

    public MutableLiveData<List<TourMateEventPojo>> eventListLD;
    public MutableLiveData<TourMateEventPojo> eventDetilsLD = new MutableLiveData<>();

    public EventDBRepository() {

        eventListLD = new MutableLiveData<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(firebaseUser.getUid());
        eventRef = userRef.child("MyEvents");
        eventRef.keepSynced(true);



        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TourMateEventPojo> events = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    events.add(d.getValue(TourMateEventPojo.class));
                }
                eventListLD.postValue(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void saveNewEventToFirebaseRTDB(TourMateEventPojo eventPojo) {
        String eventId = eventRef.push().getKey();
        eventPojo.setEventID(eventId);

        eventRef.child(eventId).setValue(eventPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(, "", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public MutableLiveData<TourMateEventPojo> getEventDetialbyEventID(String eventID) {
        eventRef.child(eventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourMateEventPojo eventPojo = dataSnapshot.getValue(TourMateEventPojo.class);

                eventDetilsLD.postValue(eventPojo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return eventDetilsLD;
    }


    public void UpdateEvent(TourMateEventPojo eventPojo) {

        String eventId = eventPojo.getEventID();
        eventPojo.setEventID(eventId);
        eventRef.child(eventId).setValue(eventPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(, "", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void DeleteEventFromEventDB(TourMateEventPojo eventPojo) {

        String eventId = eventPojo.getEventID();
        eventPojo.setEventID(eventId);

        eventRef.child(eventId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(, "", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void addMoreBudget(TourMateEventPojo eventPojo){
        String eventId = eventPojo.getEventID();
        eventPojo.setEventID(eventId);
        eventRef.child(eventId).setValue(eventPojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}
