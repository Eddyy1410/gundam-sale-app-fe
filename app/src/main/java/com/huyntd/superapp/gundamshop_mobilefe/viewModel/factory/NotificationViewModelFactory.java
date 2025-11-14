package com.huyntd.superapp.gundamshop_mobilefe.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.huyntd.superapp.gundamshop_mobilefe.repository.NotificationRepository;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.NotificationViewModel;

public class NotificationViewModelFactory implements ViewModelProvider.Factory {

    private final NotificationRepository repository;

    public NotificationViewModelFactory(NotificationRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NotificationViewModel.class)) {
            return (T) new NotificationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
