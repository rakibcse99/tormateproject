package com.example.tourmate.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.R;
import com.example.tourmate.helper.EventUtils;
import com.example.tourmate.pojos.EventExpensePojo;
import com.example.tourmate.pojos.TourMateEventPojo;
import com.example.tourmate.viewmodels.EventViewModel;
import com.example.tourmate.viewmodels.ExpenseViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventListRVAdapter extends RecyclerView.Adapter<EventListRVAdapter.EventViewHolder> {
    private List<TourMateEventPojo> eventPojos;
    private Context context;
    private EventViewModel eventViewModel = new EventViewModel();

    public EventListRVAdapter(Context context, List<TourMateEventPojo> eventPojos) {
        this.eventPojos = eventPojos;
        this.context = context;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.event_row, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.eventName.setText(eventPojos.get(position).getEventName());
        holder.tourLocation.setText(eventPojos.get(position).getDeparture()+" - "+eventPojos.get(position).getDestination());
        holder.date.setText("Departure : "+eventPojos.get(position).getDepartureDate());
        holder.budget.setText("Budget : "+String.valueOf(eventPojos.get(position).getInitialBudget())+" টাকা");
        holder.createEventDate.setText((eventPojos.get(position).getCreateEventDate()));

        String goingDate = (eventPojos.get(position).getDepartureDate());


        long diffrentDate = EventUtils.getDefferentBetweenTwoDate(EventUtils.getCrrentDate(), goingDate);
        if (diffrentDate == 0) {
            holder.dateLeft.setText("Have a safe Journey!");

        } else if (diffrentDate < 0) {
            holder.dateLeft.setText("Tour Finished!");
        } else {
            holder.dateLeft.setText(String.valueOf(EventUtils.getDefferentBetweenTwoDate(EventUtils.getCrrentDate(), goingDate)) + " Days Left");

        }


        holder.rowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventID = eventPojos.get(position).getEventID();
                final TourMateEventPojo eventPojo = eventPojos.get(position);

                final Bundle bundle = new Bundle();
                bundle.putString("id", eventID);

                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.row_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.detailMenu:
                                Navigation.findNavController(holder.itemView).navigate(R.id.mainDashBoard, bundle);
                                break;
                            case R.id.editMenu:
                                Navigation.findNavController(holder.itemView).navigate(R.id.add_Event, bundle);
                                break;
                            case R.id.deleteMenu:
                                eventViewModel.DeleteEvent(eventPojo);
                                Navigation.findNavController(holder.itemView).navigate(R.id.eventListFragment);
                                break;

                        }

                        return false;
                    }
                });


            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventID = eventPojos.get(position).getEventID();
                final TourMateEventPojo eventPojo = eventPojos.get(position);

                final Bundle bundle = new Bundle();
                bundle.putString("id", eventID);
                Navigation.findNavController(holder.itemView).navigate(R.id.eventDashBoard, bundle);


            }
        });


    }

    @Override
    public int getItemCount() {
        return eventPojos.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventName, tourLocation, date, budget, createEventDate, dateLeft;
        TextView rowMenu;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.row_eventName);
            tourLocation = itemView.findViewById(R.id.row_tourLocation);
            date = itemView.findViewById(R.id.row_date);
            budget = itemView.findViewById(R.id.row_buget);
            createEventDate = itemView.findViewById(R.id.row_createEventdate);
            dateLeft = itemView.findViewById(R.id.row_daysLeft);

            rowMenu = itemView.findViewById(R.id.row_menu);

        }
    }


}
