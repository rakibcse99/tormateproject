package com.example.tourmate.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.pojos.TourMateEventPojo;
import com.example.tourmate.repos.EventDBRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

public class EventViewModel extends ViewModel {
    private EventDBRepository eventDBRepository;
    public MutableLiveData<List<TourMateEventPojo>> eventListLD;
    public MutableLiveData<TourMateEventPojo> eventDetailsLD = new MutableLiveData<>();

    public EventViewModel() {

        eventDBRepository = new EventDBRepository();
        eventListLD = eventDBRepository.eventListLD;
    }

    public void SaveEvent(TourMateEventPojo eventPojo) {
        eventDBRepository.saveNewEventToFirebaseRTDB(eventPojo);
    }

    public void updateEvent(TourMateEventPojo eventPojo) {
        eventDBRepository.UpdateEvent(eventPojo);
    }

    public void DeleteEvent(TourMateEventPojo eventPojo) {
        eventDBRepository.DeleteEventFromEventDB(eventPojo);
    }


    public void getEventDetails(String eventID) {
        eventDetailsLD = eventDBRepository.getEventDetialbyEventID(eventID);

    }

    public void addMoreBudget(TourMateEventPojo eventPojo) {
        eventDBRepository.addMoreBudget(eventPojo);
    }


}
