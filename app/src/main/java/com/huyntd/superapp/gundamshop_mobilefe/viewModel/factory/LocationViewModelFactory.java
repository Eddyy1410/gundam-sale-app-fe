package com.huyntd.superapp.gundamshop_mobilefe.viewModel.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.huyntd.superapp.gundamshop_mobilefe.repository.LocationRepository;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.LocationViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.MessageViewModel;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationViewModelFactory implements ViewModelProvider.Factory {
    final LocationRepository repository;

    public LocationViewModelFactory (LocationRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocationViewModel.class)) {
            // Đây là nơi tạo instance ViewModel bằng cách truyền Repository vào
            return (T) new LocationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
