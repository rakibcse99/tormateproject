package com.example.tourmate.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.image_donwload_manager.DirectoryHelper;
import com.example.tourmate.image_donwload_manager.DownloadImageService;
import com.example.tourmate.pojos.LocationPicPojo;
import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.viewmodels.LocationPicViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LocationPicRVAdapter extends RecyclerView.Adapter<LocationPicRVAdapter.LocationPicViewHolder>{
    private Context context;
    private List<LocationPicPojo>  locationPicPojos;
    private LocationPicViewModel locationPicViewModel = new LocationPicViewModel();

    public LocationPicRVAdapter(Context context, List<LocationPicPojo> locationPicPojos) {
        this.context = context;
        this.locationPicPojos = locationPicPojos;
    }

    @NonNull
    @Override
    public LocationPicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationPicViewHolder(LayoutInflater.from(context).inflate(R.layout.location_pic_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationPicViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Picasso.get().load(locationPicPojos.get(position).getDownloadUrl()).fit().into(holder.locationPic);
        holder.locationPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LocationPicPojo locationPojo = locationPicPojos.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Location Photo");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view =inflater.inflate(R.layout.single_location_image,null);

                builder.setView(view);

                ImageView image = view.findViewById(R.id.locationPicView);
                Button deletebtn = view.findViewById(R.id.deleteLocationImagebtn);
                Button donwloadbtn= view.findViewById(R.id.dlocationPicBtn);

                Picasso.get().load(locationPicPojos.get(position).getDownloadUrl()).fit().into(image);

                final AlertDialog dialog = builder.create();
                dialog.show();

                deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationPicViewModel.deleteImage(locationPojo);
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                donwloadbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startService(DownloadImageService.getDownloadService(context, locationPojo.getDownloadUrl(), DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/")));

                        dialog.dismiss();
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return locationPicPojos.size();
    }


    public class LocationPicViewHolder extends RecyclerView.ViewHolder{
        private ImageView locationPic;
        public LocationPicViewHolder(@NonNull View itemView) {
            super(itemView);
            locationPic = itemView.findViewById(R.id.locat_pic_row);
        }
    }
}
