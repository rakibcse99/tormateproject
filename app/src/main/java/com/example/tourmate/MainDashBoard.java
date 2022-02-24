package com.example.tourmate;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourmate.adapter.ExpenseListRVAdpater;
import com.example.tourmate.helper.EventUtils;
import com.example.tourmate.pojos.EventExpensePojo;
import com.example.tourmate.pojos.TourMateEventPojo;
import com.example.tourmate.viewmodels.EventViewModel;
import com.example.tourmate.viewmodels.ExpenseViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainDashBoard extends Fragment {

    private String eventID = null;
    private FloatingActionButton addExpenseBtn;
    private TextView eventName, budgetTV, expenseTV, remainingTV;
    private EventViewModel eventViewModel;
    private ExpenseViewModel expenseViewModel;
    private int totalBudget = 0;

    private RecyclerView expenseRV;
    private ExpenseListRVAdpater expenseAdapter;


    public MainDashBoard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        eventViewModel = ViewModelProviders.of(requireActivity()).get(EventViewModel.class);
        expenseViewModel = ViewModelProviders.of(getActivity()).get(ExpenseViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            eventID = bundle.getString("id");
            eventViewModel.getEventDetails(eventID);
            expenseViewModel.getAllExpense(eventID);

        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_dash_board, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventName = view.findViewById(R.id.eventNameTV);
        budgetTV = view.findViewById(R.id.budgetTV);
        expenseTV = view.findViewById(R.id.expenseTV);
        remainingTV = view.findViewById(R.id.remainingTV);
        expenseRV = view.findViewById(R.id.expenseRV);

        addExpenseBtn = view.findViewById(R.id.addExpenseBtn);


        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpenseDilog();
            }
        });

        eventViewModel.eventDetailsLD.observe(this, new Observer<TourMateEventPojo>() {
            @Override
            public void onChanged(TourMateEventPojo eventPojo) {

                eventName.setText(eventPojo.getEventName());
                budgetTV.setText("Budget  " + eventPojo.getInitialBudget()+" টাকা");
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

                expenseTV.setText("Total expense  " + totalEx+" টাকা");
                remainingTV.setText("Remaining  " + remaining+" টাকা");

                expenseAdapter = new ExpenseListRVAdpater(getActivity(), eventExpensePojos);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                expenseRV.setLayoutManager(llm);
                expenseRV.setAdapter(expenseAdapter);


                expenseRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        switch (newState) {
                            case RecyclerView.SCROLL_STATE_IDLE:
                                addExpenseBtn.show();
                                break;
                            default:
                                addExpenseBtn.hide();
                                break;
                        }
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
            }
        });

    }

    private void showExpenseDilog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Expense");
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.add_expense_dialog, null);

        builder.setView(view1);
        final EditText expenseNameET = view1.findViewById(R.id.expenseNameET);
        final EditText expenseAmoutET = view1.findViewById(R.id.expenseAmountET);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ename = expenseNameET.getText().toString();
                String amount = expenseAmoutET.getText().toString();

                EventExpensePojo expensePojo = new EventExpensePojo(null, eventID, ename, Integer.parseInt(amount), EventUtils.getDateWithTime());

                expenseViewModel.saveExpense(expensePojo);


                Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        dialog.show();


    }
}
