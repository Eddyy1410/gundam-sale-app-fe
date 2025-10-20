package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.ProductRepository;

import java.util.List;

public class ProductListViewModel extends ViewModel {
    private final ProductRepository repository;
    private final LiveData<List<ProductResponse>> products;

    public ProductListViewModel() {
        repository = new ProductRepository();
        products = repository.getProducts();
    }

    public LiveData<List<ProductResponse>> getProducts() {
        return products;
    }
}