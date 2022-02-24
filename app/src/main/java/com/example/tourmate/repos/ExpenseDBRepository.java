package com.example.tourmate.repos;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tourmate.pojos.EventExpensePojo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDBRepository {

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference expenseRef;
    private FirebaseUser firebaseUser;

    public MutableLiveData<List<EventExpensePojo>> expenseListLD = new MutableLiveData<>();

    public ExpenseDBRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(firebaseUser.getUid());
        expenseRef = userRef.child("Expenses");
        expenseRef.keepSynced(true);

    }

    public void addNewExpenseToRTDB(EventExpensePojo expensePojo) {
        String expenseId = expenseRef.push().getKey();
        expensePojo.setExpenseId(expenseId);
        expenseRef.child(expensePojo.getEventId()).child(expenseId).setValue(expensePojo);

    }

    public void updateExpense(EventExpensePojo expensePojo) {
        String expenseId = expensePojo.getExpenseId();
        expensePojo.setExpenseId(expenseId);
        expenseRef.child(expensePojo.getEventId()).child(expenseId).setValue(expensePojo);

    }

    public void deleteExpenseFromDB(EventExpensePojo expensePojo) {
        String expenseId = expensePojo.getExpenseId();
        expenseRef.child(expensePojo.getEventId()).child(expenseId).removeValue();

    }


    public MutableLiveData<List<EventExpensePojo>> getAllExpenseByEventID(String eventID) {
        expenseRef.child(eventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<EventExpensePojo> expensePojos = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    expensePojos.add(d.getValue(EventExpensePojo.class));
                }
                expenseListLD.postValue(expensePojos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return expenseListLD;
    }


}
