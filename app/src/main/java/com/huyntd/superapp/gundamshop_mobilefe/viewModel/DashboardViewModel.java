package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<Integer> todayOrders = new MutableLiveData<>();
    private final MutableLiveData<Integer> pendingOrders = new MutableLiveData<>();
    private final MutableLiveData<Integer> lowStock = new MutableLiveData<>();

    public LiveData<Integer> getTodayOrders() { return todayOrders; }
    public LiveData<Integer> getPendingOrders() { return pendingOrders; }
    public LiveData<Integer> getLowStock() { return lowStock; }

    public void loadDashboardData() {
        // Giả lập API call
        todayOrders.setValue(25);
        pendingOrders.setValue(8);
        lowStock.setValue(3);
    }
}
