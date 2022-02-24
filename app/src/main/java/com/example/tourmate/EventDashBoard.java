package com.example.tourmate;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourmate.adapter.MomentRVAdapter;
import com.example.tourmate.helper.EventUtils;
import com.example.tourmate.pojos.EventExpensePojo;
import com.example.tourmate.pojos.MomentPojo;
import com.example.tourmate.pojos.TourMateEventPojo;
import com.example.tourmate.viewmodels.EventViewModel;
import com.example.tourmate.viewmodels.ExpenseViewModel;
import com.example.tourmate.viewmodels.MomentViewModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class EventDashBoard extends Fragment {

    private Button expenseBtn, momonentBtn, addmoreBudget, addLocationPic;
    private String eventID;

    private TextView eventName, budgetTV, expenseTV, remainingTV;
    private EventViewModel eventViewModel;
    private ExpenseViewModel expenseViewModel;
    private int totalBudget = 0;

    private MomentViewModel momentViewModel;
    private RecyclerView momentRV;
    private MomentRVAdapter rvAdapter;

    private TourMateEventPojo event;

    //Tour Balace progree show
    private LinearLayout balanceLayout;
    private ProgressBar progressBar;
    private TextView currentBalanceTvId, expensePersentageTv, budExTv;
    private NumberFormat nf = new DecimalFormat("##.###");

    public EventDashBoard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        expenseViewModel = ViewModelProviders.of(getActivity()).get(ExpenseViewModel.class);
        momentViewModel = ViewModelProviders.of(getActivity()).get(MomentViewModel.class);


        Bundle bundle = getArguments();
        if (bundle != null) {
            eventID = bundle.getString("id");
            eventViewModel.getEventDetails(eventID);
            expenseViewModel.getAllExpense(eventID);
            momentViewModel.getMoments(eventID);

        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_dash_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = new Bundle();
        bundle.putString("id", eventID);

        balanceLayout = view.findViewById(R.id.linlayID);
        progressBar = view.findViewById(R.id.progressBar);
        currentBalanceTvId = view.findViewById(R.id.currenBalanceDisplayTvId);
        expensePersentageTv = view.findViewById(R.id.expensePersentageTvId);
        budExTv = view.findViewById(R.id.budExTvId);

        expenseBtn = view.findViewById(R.id.eventExpensebtn);
        momonentBtn = view.findViewById(R.id.eventMomentbtn);
        addmoreBudget = view.findViewById(R.id.addmoreBudgetBtn);
        addLocationPic = view.findViewById(R.id.locationPicBtn);
        momentRV = view.findViewById(R.id.momentRV);


        eventName = view.findViewById(R.id.eventNameTV);
        budgetTV = view.findViewById(R.id.budgetTV);
        expenseTV = view.findViewById(R.id.expenseTV);
        //remainingTV = view.findViewById(R.id.remainingTV);


        expenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.mainDashBoard, bundle);
            }
        });

        momonentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.momentGallary, bundle);
            }
        });

        addmoreBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMoreBudgetDialog();
            }
        });

        addLocationPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.locationPic, bundle);
            }
        });

        eventViewModel.eventDetailsLD.observe(this, new Observer<TourMateEventPojo>() {
            @Override
            public void onChanged(TourMateEventPojo eventPojo) {
                event = eventPojo;
                eventName.setText(eventPojo.getEventName());
                budgetTV.setText("বাজেট : " + eventPojo.getInitialBudget() + "/- টাকা");
                totalBudget = eventPojo.getInitialBudget();
            }
        });

        expenseViewModel.expenseListLD.observe(this, new Observer<List<EventExpensePojo>>() {
            @Override
            public void onChanged(List<EventExpensePojo> eventExpensePojos) {
                int totalEx = 0;
                int remaining = 0;


                for (EventExpensePojo expensePojo : eventExpensePojos) {
                    totalEx += expensePojo.getAmount();
                }
                remaining = totalBudget - totalEx;

                expenseTV.setText("মোট খরচ : " + totalEx + "/- টাকা");
                //remainingTV.setText("Remaining  " + remaining+" টাকা");

                currentBalanceTvId.setText("বর্তমান ব্যালেন্স : " + remaining + "/- টাকা");

                double consumed2 = (Double.valueOf(totalEx) * 100) / Double.valueOf(totalBudget);
                expensePersentageTv.setText(String.valueOf(nf.format(consumed2)) + "%");

                budExTv.setText(totalEx + "/" + totalBudget);

                showProgressBar(totalEx, totalBudget);

            }
        });


        momentViewModel.momentsLD.observe(this, new Observer<List<MomentPojo>>() {
            @Override
            public void onChanged(List<MomentPojo> momentPojos) {
                rvAdapter = new MomentRVAdapter(getActivity(), momentPojos);
                GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
                momentRV.setLayoutManager(glm);
                momentRV.setHasFixedSize(true);
                momentRV.setItemViewCacheSize(10);
                momentRV.setDrawingCacheEnabled(true);
                momentRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                momentRV.setAdapter(rvAdapter);

            }
        });


        //budget progress

        createProgressBar();

    }

    private void showProgressBar(int totalEx, int budget) {
        if (totalEx >= 0) {
            calculateProgress(totalEx, budget);
        } else
            Toast.makeText(getContext(), "Sorry! No Ammount is remainnig.", Toast.LENGTH_SHORT).show();
    }

    private void calculateProgress(int totalEx, int budget) {
        if (totalEx >= 0) {

            //consumed = (expenditure * 100) / budget;
            double consumed3 = (Double.valueOf(totalEx) * 100) / Double.valueOf(budget);
            progressBar.setProgress(Integer.valueOf((int) consumed3));

            if (consumed3 >= 90) {
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FCE70F0F")));
            } else if (consumed3 >= 70) {
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
            } else if (consumed3 >= 50) {
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#2ecc71")));
            }

        } else Toast.makeText(getContext(), "please enter some ammount", Toast.LENGTH_SHORT).show();
    }

    private void createProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.showContextMenu();
        progressBar.setScaleY(5f);
    }

    private void showAddMoreBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add More Budget");
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.add_more_budget_dialog, null);

        builder.setView(view1);
        final EditText expenseAmoutET = view1.findViewById(R.id.moreBudgetAmountET);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = expenseAmoutET.getText().toString();
                String eventName = event.getEventName();
                String startLocation = event.getDeparture();
                String destination = event.getDestination();
                String departureDate = event.getDepartureDate();
                int budget = event.getInitialBudget();
                int totalBudget = budget + Integer.parseInt(amount);
                TourMateEventPojo tourMateEventPojo = new TourMateEventPojo(eventID, eventName, startLocation, destination, totalBudget, departureDate, EventUtils.getDateWithTime());
                eventViewModel.addMoreBudget(tourMateEventPojo);
                Navigation.findNavController(getView()).navigate(R.id.eventDashBoard);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
