package com.example.tourmate;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tourmate.viewmodels.LoginViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText emailET, passwordET;
    private Button loginbtn, registerbtn;
    private TextView showError;
    private LoginViewModel loginViewModel;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
//        (getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailET = view.findViewById(R.id.inputEmail);
        passwordET = view.findViewById(R.id.inputPassword);

        showError = view.findViewById(R.id.showErroText);

        loginbtn = view.findViewById(R.id.loginbtn);
        registerbtn = view.findViewById(R.id.registrationBtn);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailET.getText().toString();
                String pass = passwordET.getText().toString();

                if (email.isEmpty() && pass.isEmpty()) {
                    emailET.setError("Input a email Address");
                    passwordET.setError("Input a password");
                } else {
                    loginViewModel.login(email, pass);
                }


            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String pass = passwordET.getText().toString();
                loginViewModel.register(email, pass);
            }
        });


        loginViewModel.stateLiveData.observe(this, new Observer<LoginViewModel.AuthenticationState>() {
            @Override
            public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                switch (authenticationState) {
                    case AUTHENTICATED:
                        Navigation.findNavController(view).navigate(R.id.eventListFragment);
                        break;
                    case UNAUTHENTICATED:
                        break;
                }
            }
        });


        loginViewModel.errMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showError.setText(s);
            }
        });


    }


}
