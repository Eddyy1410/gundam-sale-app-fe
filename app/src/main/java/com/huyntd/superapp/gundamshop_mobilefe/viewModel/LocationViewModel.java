package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.LocationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.LocationRepository;

import java.util.List;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationViewModel extends ViewModel {
    final LocationRepository repository;
    final LiveData<List<LocationResponse>> locations;

    public LocationViewModel(LocationRepository repository) {
        this.repository = repository;
        this.locations = repository.getLocations();
    }

    public LiveData<List<LocationResponse>> getLocations() {
        return locations;
    }
}
