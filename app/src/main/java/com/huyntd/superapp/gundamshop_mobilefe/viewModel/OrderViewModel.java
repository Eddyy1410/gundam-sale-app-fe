package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.request.CreateOrderRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private final OrderRepository orderRepository = OrderRepository.getInstance();

    public LiveData<List<OrderResponse>> getOrdersByUserId(int userId) {
        return orderRepository.getOrdersByUserId(userId);
    }

    public LiveData<OrderResponse> getOrderDetail(int orderId){
        return orderRepository.getOrdersById(orderId);
    }

    public LiveData<List<OrderResponse>> getOrdersByStatus(int userId, String status) {
        return orderRepository.getOrdersByStatus(userId, status);
    }

    public LiveData<OrderResponse> createNewOrder(CreateOrderRequest request, boolean status) {
        return orderRepository.createOrder(request, status);
    }

}

