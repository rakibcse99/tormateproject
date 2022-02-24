package com.example.tourmate.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.LocationPic;
import com.example.tourmate.MomentGallary;
import com.example.tourmate.pojos.LocationPicPojo;
import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.repos.LocationPicRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LocationPicViewModel extends ViewModel {
    private LocationPicRepository locationPicRepository;
    public MutableLiveData<List<LocationPicPojo>> locationPicLD = new MutableLiveData<>();

    public LocationPicViewModel() {
        locationPicRepository = new LocationPicRepository();
    }

    public void uploadImageToFirebaseStorage(final Context context, File file, final String eventId) {
        LocationPic.uploadProgressBar.setVisibility(View.VISIBLE);
        Toast.makeText(context, "Wait Uploading", Toast.LENGTH_SHORT).show();

        StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        Uri fileUri = Uri.fromFile(file);
        final StorageReference imageRef = rootRef.child("LocationImages/" + fileUri.getLastPathSegment());

        ///For image Compress
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);


        //For get URI Link of Image

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL

                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                    LocationPic.uploadProgressBar.setVisibility(View.GONE);
                    Uri downloadUri = task.getResult();
                    LocationPicPojo moments = new LocationPicPojo(null, eventId, downloadUri.toString());
                    locationPicRepository.addNewLocationPic(moments);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    public void getLocationMoments(String eventId) {

        locationPicLD = locationPicRepository.getAllLocationPics(eventId);
    }

    public void deleteImage(LocationPicPojo locationPicPojo) {

        locationPicRepository.deleteImagefromDB(locationPicPojo);
    }
}
