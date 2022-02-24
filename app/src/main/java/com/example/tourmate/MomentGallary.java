package com.example.tourmate;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tourmate.adapter.MomentRVAdapter;
import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.viewmodels.MomentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MomentGallary extends Fragment {

    private static final String TAG = MomentGallary.class.getSimpleName();

    private static final int REQUEST_CAMERA_CODE = 123;
    private static final int REQUEST_STORAGE_CODE = 321;
    private FloatingActionButton takePicBtn;
    private String currentPhotoPath;
    private MomentViewModel momentViewModel;
    private String eventId;
    private RecyclerView momentPicRV;
    private MomentRVAdapter momentRVAdapter;
    public static ProgressBar uploadProgressBar;


    public MomentGallary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        momentViewModel = ViewModelProviders.of(this).get(MomentViewModel.class);
        Bundle bundle = getArguments();

        if (bundle != null) {
            eventId = bundle.getString("id");
            momentViewModel.getMoments(eventId);
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moment_gallary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        takePicBtn = view.findViewById(R.id.takeaPicbtn);
        momentPicRV = view.findViewById(R.id.momentPicRV);
        uploadProgressBar = view.findViewById(R.id.uploadingProgress);

        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    dispatchCameraIntent();
                }
            }
        });

        momentViewModel.momentsLD.observe(this, new Observer<List<MomentPojo>>() {
            @Override
            public void onChanged(List<MomentPojo> momentPojos) {
                momentRVAdapter = new MomentRVAdapter(getActivity(), momentPojos);
                GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
                momentPicRV.setLayoutManager(glm);

                momentPicRV.setHasFixedSize(true);
                momentPicRV.setItemViewCacheSize(20);
                momentPicRV.setDrawingCacheEnabled(true);
                momentPicRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                momentPicRV.setAdapter(momentRVAdapter);


                uploadProgressBar.setVisibility(View.GONE);
                momentPicRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                takePicBtn.show();
                                break;
                            default:
                                takePicBtn.hide();
                                break;
                        }
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });


            }
        });

    }

    private boolean checkStoragePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_STORAGE_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_CODE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permission accepted", Toast.LENGTH_SHORT).show();
            dispatchCameraIntent();
        }
    }


    private void dispatchCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                        "com.example.tourmate",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        /* Create an image file name */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_CODE &&
                resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult: " + currentPhotoPath);
            File file = new File(currentPhotoPath);
            momentViewModel.uploadImageToFirebaseStorage(getActivity(), file, eventId);
        }
    }

}
