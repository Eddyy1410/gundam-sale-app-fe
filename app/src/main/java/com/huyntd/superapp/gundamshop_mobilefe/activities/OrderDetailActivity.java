package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.OrderDetailAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.OrdersAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.UserViewModel;

public class OrderDetailActivity extends AppCompatActivity {
    OrderDetailAdapter adapter;
    TextView tvOrderCode, tvOrderDate, tvStatusDelivered;
    TextView tvTotalPrice, tvPaymentMethod, tvShipping, tvFinalPrice;

    TextView tvCustomerName, tvCustomerEmail, tvCustomerPhone, tvCustomerAddress;
    RecyclerView rvOrderItems;
    Button btnBack;

    OrderViewModel orderViewModel;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        // Đảm bảo layout tránh vùng camera và status bar
        View rootView = findViewById(R.id.order_main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // Lấy kích thước phần đệm của hệ thống (status bar, camera, navigation bar)
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Thêm padding tương ứng để tránh chồng lên vùng notch/camera
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Ánh xạ view ---
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvStatusDelivered = findViewById(R.id.tvStatusDelivered);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvShipping = findViewById(R.id.tvShipping);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerEmail = findViewById(R.id.tvCustomerEmail);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        rvOrderItems = findViewById(R.id.rvOrderItems);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("orderId",0);

        // 🔹 Lấy ViewModel (chuẩn AndroidX)
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 🔹 Gọi API qua ViewModel
        //1. Api lấy thông tin người dùng
        userViewModel.getInfo().observe(this, userResponse -> {
            if (userResponse != null){
                tvCustomerName.setText(userResponse.getFullName());
                tvCustomerPhone.setText(userResponse.getPhone());
                tvCustomerEmail.setText(userResponse.getEmail());
                tvCustomerAddress.setText("Hồ Chí Minh");
            }
        });

        // 2. Api lấy lịch sử mua hàng
        orderViewModel.getOrderDetail(orderId).observe(this, order -> {
            if (order != null) {

                tvFinalPrice.setText(String.format("%,.0fđ", order.getTotalPrice()));

                adapter = new OrderDetailAdapter(
                        this,
                        order.getOrderItems()
                );
                tvOrderCode.setText("Mã đơn hàng: " + order.getId());
                tvOrderDate.setText(order.getOrderDate());
                tvTotalPrice.setText(String.format("%,.0fđ", order.getTotalPrice()));

                //Chỉnh màu trạng thái đơn hàng
                tvStatusDelivered.setText(order.getStatus());
                // Màu mặc định
                int color = Color.parseColor("#FFD700"); // vàng
                String status = order.getStatus().toUpperCase();

                if (status.equals("CANCELLED") || status.equals("RETURNED")) {
                    color = Color.parseColor("#FF0000"); // đỏ
                } else if (status.equals("DELIVERED")) {
                    color = Color.parseColor("#008000"); // xanh lá
                }

                // Tạo viền động
                GradientDrawable bg = new GradientDrawable();
                bg.setShape(GradientDrawable.RECTANGLE);
                bg.setCornerRadius(20);
                bg.setStroke(3, color);
                bg.setColor(Color.TRANSPARENT);

                tvStatusDelivered.setTextColor(color);
                tvStatusDelivered.setBackground(bg);

                tvPaymentMethod.setText(order.getPaymentMethod());
                tvShipping.setText(order.getBillingAddress());

                rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
                rvOrderItems.setAdapter(adapter);
            } else {
                Log.d("OrderHistory", "Không có đơn hàng nào.");
            }
        });



        btnBack.setOnClickListener(v -> finish());

    }
}