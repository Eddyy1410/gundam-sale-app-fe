package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.OrdersAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;

import java.util.ArrayList;


public class OrderHistoryActivity extends AppCompatActivity {

    RecyclerView rvOrders;
    OrdersAdapter adapter;
    TextView tvOrdersCount, tvTotal, tvName, tvPhone;
    ImageView imgAvatar;

    OrderViewModel orderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);

        // Đảm bảo layout tránh vùng camera và status bar
        View rootView = findViewById(R.id.order_main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // Lấy kích thước phần đệm của hệ thống (status bar, camera, navigation bar)
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Thêm padding tương ứng để tránh chồng lên vùng notch/camera
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        rvOrders = findViewById(R.id.rv_orders);
        tvOrdersCount = findViewById(R.id.tv_orders_count);
        tvTotal = findViewById(R.id.tv_total);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        imgAvatar = findViewById(R.id.img_avatar);

        // 🔹 Lấy ViewModel (chuẩn AndroidX)
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // 🔹 Gọi API qua ViewModel
        int userId = 2; // hoặc lấy từ SharedPreferences
        orderViewModel.getOrdersByUserId(userId).observe(this, orders -> {
            if (orders != null && !orders.isEmpty()) {
                tvOrdersCount.setText(String.valueOf(orders.size()));

                // ✅ Tính tổng tiền (nếu có field totalPrice trong OrderResponse)
                double total = 0;
                for (OrderResponse o : orders) {
                    total += o.getTotalPrice();
                }
                tvTotal.setText(String.format("%,.0fđ", total));

                adapter = new OrdersAdapter(
                        this,
                        orders,
                        new OrdersAdapter.OnItemClickListener() {
                            @Override
                            public void onDetailClick(OrderResponse item) {
                                // TODO: mở chi tiết đơn hàng
                            }
                        },
                        orderViewModel
                );


                rvOrders.setLayoutManager(new LinearLayoutManager(this));
                rvOrders.setAdapter(adapter);
            } else {
                Log.d("OrderHistory", "Không có đơn hàng nào.");
            }
        });
    }
}