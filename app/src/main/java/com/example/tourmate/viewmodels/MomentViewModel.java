package com.example.tourmate.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.MomentGallary;
import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.repos.MomentRepository;
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

public class MomentViewModel extends ViewModel {
    private MomentRepository momentRepository;
    public MutableLiveData<List<MomentPojo>> momentsLD = new MutableLiveData<>();

    public MomentViewModel() {

        momentRepository = new MomentRepository();
    }

    public void uploadImageToFirebaseStorage(final Context context, File file, final String eventId) {
/*
        StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        Uri fileUri = Uri.fromFile(file);
        final StorageReference imageRef = rootRef.child("EventImages/" + fileUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(fileUri);
*/
        MomentGallary.uploadProgressBar.setVisibility(View.VISIBLE);
        Toast.makeText(context, "Wait Uploading", Toast.LENGTH_SHORT).show();

        StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        Uri fileUri = Uri.fromFile(file);
        final StorageReference imageRef = rootRef.child("EventImages/" + fileUri.getLastPathSegment());

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
                    MomentGallary.uploadProgressBar.setVisibility(View.GONE);
                    Uri downloadUri = task.getResult();
                    MomentPojo moments = new MomentPojo(null, eventId, downloadUri.toString());
                    momentRepository.addNewMoment(moments);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    public void getMoments(String eventId) {

        momentsLD = momentRepository.getAllMoments(eventId);
    }

    public void deleteImage(MomentPojo momentPojo) {

        momentRepository.deleteImagefromDB(momentPojo);
    }


}
