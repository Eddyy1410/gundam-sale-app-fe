package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.ProductRepository;

import java.util.List;

public class ProductListViewModel extends ViewModel {
    private final ProductRepository repository;
    private final MediatorLiveData<List<ProductResponse>> products = new MediatorLiveData<>();
    private LiveData<List<ProductResponse>> currentSource;
    public ProductListViewModel() {
        repository = new ProductRepository();
    }

    public LiveData<List<ProductResponse>> getProducts(String sort) {
        return repository.getProducts(sort);
    }

    public void loadProducts(String sort) {
        LiveData<List<ProductResponse>> source = repository.getProducts(sort);

        // remove previous source để tránh nhiều observer/rò rỉ
        if (currentSource != null) {
            products.removeSource(currentSource);
        }

        currentSource = source;
        products.addSource(source, productResponses -> products.setValue(productResponses));
    }
}