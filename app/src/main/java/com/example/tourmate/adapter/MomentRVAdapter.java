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

import com.example.tourmate.image_donwload_manager.DirectoryHelper;
import com.example.tourmate.image_donwload_manager.DownloadImageService;
import com.example.tourmate.R;
import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.viewmodels.MomentViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MomentRVAdapter extends RecyclerView.Adapter<MomentRVAdapter.MomentViewHolder>{
    private Context context;
    private List<MomentPojo> momentPojos;
    private MomentViewModel momentViewModel = new MomentViewModel();


    public MomentRVAdapter(Context context, List<MomentPojo> momentPojos) {
        this.context = context;
        this.momentPojos = momentPojos;


    }

    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MomentViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.gallery_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Picasso.get().load(momentPojos.get(position).getDownloadUrl()).fit().into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MomentPojo momentPojo = momentPojos.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Moments Photo");
                LayoutInflater inflater = LayoutInflater.from(context);
                View view =inflater.inflate(R.layout.single_image_dialog,null);

                builder.setView(view);

                ImageView image = view.findViewById(R.id.imageView);
                Button deletebtn = view.findViewById(R.id.deleteImagebtn);
                Button donwloadbtn= view.findViewById(R.id.downlaodbtn);

                Picasso.get().load(momentPojos.get(position).getDownloadUrl()).fit().into(image);

                final AlertDialog dialog = builder.create();
                dialog.show();

                deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        momentViewModel.deleteImage(momentPojo);
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                donwloadbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startService(DownloadImageService.getDownloadService(context, momentPojo.getDownloadUrl(), DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/")));

                        dialog.dismiss();
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return momentPojos.size();
    }

    public class MomentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MomentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_row_moment);

        }
    }


}
