package com.example.tourmate.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.pojos.EventExpensePojo;
import com.example.tourmate.repos.ExpenseDBRepository;

import java.util.List;

public class ExpenseViewModel extends ViewModel {
    ExpenseDBRepository expenseDBRepository;
    public MutableLiveData<List<EventExpensePojo>> expenseListLD = new MutableLiveData<>();

    public ExpenseViewModel() {
        expenseDBRepository = new ExpenseDBRepository();
    }

    public void saveExpense(EventExpensePojo expensePojo) {
        expenseDBRepository.addNewExpenseToRTDB(expensePojo);
    }

    public void updateExpense(EventExpensePojo expensePojo) {
        expenseDBRepository.updateExpense(expensePojo);
    }

    public void DeleteExpense(EventExpensePojo expensePojo) {
        expenseDBRepository.deleteExpenseFromDB(expensePojo);
    }


    public void getAllExpense(String eventID) {
        expenseListLD = expenseDBRepository.getAllExpenseByEventID(eventID);
    }

}
