package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.UserRepository;

import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository  = UserRepository.getInstance();

    public LiveData<UserResponse> getInfo () {
        return userRepository.getInfo();
    }

}
