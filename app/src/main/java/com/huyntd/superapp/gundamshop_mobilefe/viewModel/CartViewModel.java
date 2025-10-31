package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.CartResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.CartRepository;

public class CartViewModel extends ViewModel {
    private final CartRepository cartRepository = CartRepository.getInstance();

    public LiveData<CartResponse> getCartsByUserId(int userId) {
        return cartRepository.getCartByUserId(userId);
    }
}
