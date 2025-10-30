package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.enums.OrderStatus;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class QuickOrderViewModel extends ViewModel {

    private static final String TAG = "QuickOrderViewModel";

    private final OrderRepository orderRepository = OrderRepository.getInstance();

    private final MutableLiveData<List<OrderResponse>> todayOrders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLastPage = new MutableLiveData<>(false);

    private final MutableLiveData<OrderResponse> orderDetail  = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private int currentPage = 0;
    private static final int PAGE_SIZE = 2;

    public LiveData<List<OrderResponse>> getTodayOrders() { return todayOrders; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsLastPage() { return isLastPage; }

    public LiveData<OrderResponse> getOrderDetail() {
        return orderDetail;
    }

    // ------------------ LOAD DATA ------------------
    public void loadTodayOrders(boolean refresh) {
        if (isLoading.getValue() != null && isLoading.getValue()) return;

        isLoading.setValue(true);
        if (refresh) currentPage = 0;

        Log.d(TAG, "📡 Loading orders today: page=" + currentPage);

        orderRepository.getOrdersToday(
                currentPage,
                PAGE_SIZE,
                "orderDate",
                "desc",
                "PENDING",
                new OrderRepository.RepositoryCallback<ApiResponse<PageResponse<OrderResponse>>>() {
                    @Override
                    public void onSuccess(ApiResponse<PageResponse<OrderResponse>> result) {
                        isLoading.postValue(false);

                        if (result != null && result.isSuccess() && result.getResult() != null) {
                            List<OrderResponse> newOrders = result.getResult().getContent();
                            Log.d(TAG, "✅ Loaded " + newOrders.size() + " orders from API");

                            if (refresh) {
                                todayOrders.postValue(newOrders);
                            } else {
                                List<OrderResponse> current = todayOrders.getValue();
                                if (current != null) current.addAll(newOrders);
                                todayOrders.postValue(current);
                            }

                            // ✅ Check if last page
                            boolean last = result.getResult().isLast();
                            isLastPage.postValue(last);

                            if (!last) {
                                currentPage++;
                            }
                        } else {
                            Log.w(TAG, "⚠️ API returned empty or invalid data");
                            if (refresh) todayOrders.postValue(new ArrayList<>());
                            isLastPage.postValue(true);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.postValue(false);
                        Log.e(TAG, "🚨 Failed to load orders today: " + error);
                    }
                }
        );
    }

    // ------------------ LOAD NEXT PAGE ------------------
    public void loadNextPage() {
        Boolean last = isLastPage.getValue();
        Boolean loading = isLoading.getValue();

        if (Boolean.TRUE.equals(last) || Boolean.TRUE.equals(loading)) return;
        loadTodayOrders(false);
    }

//    ------------------- UPDATE ORDER STATUS ------------------
public void updateOrderStatus(int orderId, OrderStatus newStatus) {
    isLoading.setValue(true);
    System.out.println("🔄 Updating order ID " + orderId + " to status: " + newStatus.name());
    // Gửi status lên server
    orderRepository.updateOrderStatus(orderId, newStatus.name(), new OrderRepository.RepositoryCallback<OrderResponse>() {
        @Override
        public void onSuccess(OrderResponse result) {
            isLoading.postValue(false);
            // Cập nhật LiveData
            orderDetail.postValue(result);
            Log.d(TAG, "✅ Updated order status to: " + newStatus.name());
        }

        @Override
        public void onError(String error) {
            isLoading.postValue(false);
            errorMessage.postValue(error);
            Log.e(TAG, "🚨 Failed to update status: " + error);
        }
    });
}


    // ------------------ REFRESH ------------------
    public void refresh() {
        loadTodayOrders(true);
    }
}
