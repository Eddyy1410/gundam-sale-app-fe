package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.ProductRepository;

import java.util.List;


public class ProductDetailViewModel extends ViewModel {

    private final ProductRepository repository;
    private final MutableLiveData<ProductResponse> productLiveData = new MutableLiveData<>();
    private LiveData<List<ProductResponse>> relatedProducts;

    public ProductDetailViewModel() {
        repository = new ProductRepository();
    }

    public LiveData<ProductResponse> getProductLiveData() {
        return productLiveData;
    }
    public LiveData<List<ProductResponse>> getRelatedProductsLiveData() {
        return relatedProducts;
    }

    public void loadProductDetail(int productId) {
        repository.getProductDetail(productId).observeForever(product -> {
            productLiveData.setValue(product);
        });
    }

    public LiveData<List<ProductResponse>> loadRelatedProducts(int categoryId) {
        if (relatedProducts == null) {
            relatedProducts = repository.getRelatedProducts(categoryId);
        }
        return relatedProducts;
    }
}
