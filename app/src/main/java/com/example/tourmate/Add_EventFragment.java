package com.example.tourmate;


import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.tourmate.helper.EventUtils;
import com.example.tourmate.pojos.TourMateEventPojo;
import com.example.tourmate.viewmodels.EventViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Add_EventFragment extends Fragment {

    private Button createEvnBtn, updateEventBtn;
    private TextInputEditText eventNameET, startLocationET, departureDateET, destinationET, budgetET;
    private String departureDate;
    private EventViewModel eventViewModel;
    private String eventID;


    public Add_EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            eventID = bundle.getString("id");

            eventViewModel.getEventDetails(eventID);

            eventViewModel.eventDetailsLD.observe(this, new Observer<TourMateEventPojo>() {
                @Override
                public void onChanged(TourMateEventPojo eventPojo) {

                    eventNameET.setText(eventPojo.getEventName());
                    startLocationET.setText(eventPojo.getDeparture());
                    destinationET.setText(eventPojo.getDestination());
                    departureDateET.setText(eventPojo.getDepartureDate());

                    budgetET.setText(String.valueOf(eventPojo.getInitialBudget()));
                    createEvnBtn.setVisibility(View.GONE);
                    updateEventBtn.setVisibility(View.VISIBLE);

                    departureDate = eventPojo.getDepartureDate();

                }
            });

        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventNameET = view.findViewById(R.id.eventNameET);
        startLocationET = view.findViewById(R.id.startLocationET);
        destinationET = view.findViewById(R.id.destinationET);
        budgetET = view.findViewById(R.id.budgetET);
        createEvnBtn = view.findViewById(R.id.createEventbtn);
        departureDateET = view.findViewById(R.id.departureDateET);
        updateEventBtn = view.findViewById(R.id.upateEventBtn);


        createEvnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameET.getText().toString();
                String startLocation = startLocationET.getText().toString();
                String destination = destinationET.getText().toString();
                String budget = budgetET.getText().toString();

                TourMateEventPojo tourMateEventPojo = new TourMateEventPojo(null, eventName, startLocation, destination, Integer.parseInt(budget), departureDate, EventUtils.getDateWithTime());
                eventViewModel.SaveEvent(tourMateEventPojo);

                Navigation.findNavController(view).navigate(R.id.eventListFragment);

            }
        });

        departureDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateaDilogPicker();
            }
        });


        updateEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eventName = eventNameET.getText().toString();
                String startLocation = startLocationET.getText().toString();
                String destination = destinationET.getText().toString();
                String budget = budgetET.getText().toString();
                TourMateEventPojo tourMateEventPojo = new TourMateEventPojo(eventID, eventName, startLocation, destination, Integer.parseInt(budget), departureDate, EventUtils.getDateWithTime());
                eventViewModel.updateEvent(tourMateEventPojo);
                Navigation.findNavController(view).navigate(R.id.eventListFragment);


            }
        });


    }

    private void showDateaDilogPicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                dateSetListener, year, month, day);
        dpd.show();


    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            departureDate = new SimpleDateFormat("dd/MM/yyyy")
                    .format(calendar.getTime());
            departureDateET.setText(departureDate);


        }
    };

}
