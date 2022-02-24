package com.example.tourmate.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmate.repos.FirebaseLoginRepository;

public class LoginViewModel extends ViewModel {
    private FirebaseLoginRepository firebaseLoginRepository;
    public MutableLiveData<AuthenticationState> stateLiveData;
    public MutableLiveData<String> errMsg = new MutableLiveData<>();

    public enum AuthenticationState
    {
        AUTHENTICATED,
        UNAUTHENTICATED,
    }
    public LoginViewModel() {
        stateLiveData = new MutableLiveData<>();
        firebaseLoginRepository = new FirebaseLoginRepository(stateLiveData);

        errMsg = firebaseLoginRepository.getErrMsg();

        if (firebaseLoginRepository.getFirebaseUser() != null)
        {
            stateLiveData.postValue(AuthenticationState.AUTHENTICATED);
        }
        else
        {
            stateLiveData.postValue(AuthenticationState.UNAUTHENTICATED);
        }
    }

    public void login(String email,String pass)
    {
       stateLiveData = firebaseLoginRepository.LoginFirebaseUser(email,pass);
    }
    public void register(String email,String pass)
    {
        stateLiveData =  firebaseLoginRepository.RegisterFireBaseUser(email,pass);
    }

}
