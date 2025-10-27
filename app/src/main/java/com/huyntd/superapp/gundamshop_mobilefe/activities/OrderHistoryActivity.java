package com.huyntd.superapp.gundamshop_mobilefe.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.OrdersAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;


public class OrderHistoryActivity extends AppCompatActivity {

    RecyclerView rvOrders;
    OrdersAdapter adapter;
    TextView tvOrdersCount, tvTotal, tvName, tvPhone, tvEmpty;
    ImageView imgAvatar;
    ImageButton btnBack;
    OrderViewModel orderViewModel;
    UserViewModel userViewModel;
    int userId = 0;
    Button btnAll, btnPending, btnProcessing, btnShipped, btnDelivered, btnCancelled, btnRefunded;

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
        mappingViews();

        // 🔹 Lấy ViewModel (chuẩn AndroidX)
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 🔹 Gọi API qua ViewModel
        //1. Api lấy thông tin người dùng
        userViewModel.getInfo().observe(this, userResponse -> {
            if (userResponse != null) {
                Glide.with(this)
                        .load("https://i.pinimg.com/736x/30/a8/49/30a8490ff409df33d1e23702cf2c4aa8.jpg")
                        .override(300, 300) // fix size 200x200 pixel
                        .centerCrop()       // cắt giữa hình để không méo
                        .into(imgAvatar);

                tvName.setText(userResponse.getFullName());
                tvPhone.setText(userResponse.getPhone());
                userId = userResponse.getId();

                // 2. Api lấy tổng tiền + đơn hàng
                orderViewModel.getOrdersByUserId(userId).observe(this, orders -> {
                    if (orders != null && !orders.isEmpty()) {
                        tvOrdersCount.setText(String.valueOf(orders.size()));

                        // ✅ Tính tổng tiền (nếu có field totalPrice trong OrderResponse)
                        double total = 0;
                        for (OrderResponse o : orders) {
                            if (o.getStatus().equals("DELIVERED")) {
                                total += o.getTotalPrice();
                            }
                        }
                        tvTotal.setText(String.format("%,.0fđ", total));
                    } else {
                        Log.d("OrderHistory", "Không có đơn hàng nào.");
                    }
                });

                // 3. Api lấy lịch sử mua hàng
                // ===== Gắn listener =====
                setupListeners();

                //Gắn các button để lọc status
                orderViewModel.getOrdersByUserId(userId).observe(this, orders -> {
                    updateUI(orders);
                });

            }
        });

    }

    private void mappingViews() {
        rvOrders = findViewById(R.id.rv_orders);
        tvOrdersCount = findViewById(R.id.tv_orders_count);
        tvTotal = findViewById(R.id.tv_total);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        imgAvatar = findViewById(R.id.img_avatar);
        btnBack = findViewById(R.id.btn_back);
        btnAll = findViewById(R.id.btn_all);
        btnPending = findViewById(R.id.btn_pending);
        btnProcessing = findViewById(R.id.btn_processing);
        btnShipped = findViewById(R.id.btn_shipped);
        btnCancelled = findViewById(R.id.btn_cancelled);
        btnDelivered = findViewById(R.id.btn_delivered);
        btnRefunded = findViewById(R.id.btn_refunded);
        tvEmpty = findViewById(R.id.tv_empty);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // ✅ Lắng nghe click filter, userId có thể = 0 lúc đầu nhưng sẽ cập nhật sau
        btnAll.setOnClickListener(v -> {
            loadOrdersByStatus(userId, null);
            setActiveButton(btnAll);
        });
        btnPending.setOnClickListener(v -> {
            loadOrdersByStatus(userId, "PENDING");
            setActiveButton(btnPending);
        });
        btnProcessing.setOnClickListener(v -> {
            loadOrdersByStatus(userId, "PROCESSING");
            setActiveButton(btnProcessing);
        });
        btnShipped.setOnClickListener(v -> {
            loadOrdersByStatus(userId, "SHIPPED");
            setActiveButton(btnShipped);
        });
        btnDelivered.setOnClickListener(v -> {
            loadOrdersByStatus(userId, "DELIVERED");
            setActiveButton(btnDelivered);
        });
        btnCancelled.setOnClickListener(v -> {
            loadOrdersByStatus(userId, "CANCELLED");
            setActiveButton(btnCancelled);
        });
        btnRefunded.setOnClickListener(v -> {
            loadOrdersByStatus(userId, "REFUNDED");
            setActiveButton(btnRefunded);
        });
    }

    //--------------Đổi màu nút--------------------
    private void setActiveButton(Button activeButton) {
        // Reset tất cả về mặc định
        Button[] buttons = {btnAll, btnPending, btnProcessing, btnShipped, btnDelivered, btnCancelled, btnRefunded};
        for (Button btn : buttons) {
            btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            btn.setTextColor(getResources().getColor(android.R.color.black));
        }

        // Gán cho nút được chọn
        activeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        activeButton.setTextColor(getResources().getColor(android.R.color.white));
    }

    //----------------------Load API---------------------------
    private void loadOrdersByStatus(int userId, @Nullable String status) {
        if (status == null) {
            orderViewModel.getOrdersByUserId(userId)
                    .observe(this, orders -> {
                        updateUI(orders);
                    });
        } else {
            orderViewModel.getOrdersByStatus(userId, status)
                    .observe(this, orders -> {
                        updateUI(orders);
                    });
        }
    }

    //----------------------Gắn UI vào Adapter----------------------
    private void updateUI(List<OrderResponse> orders) {
        if (orders != null && !orders.isEmpty()) {
            tvEmpty.setVisibility(View.GONE); // Ẩn thông báo trống
            rvOrders.setVisibility(View.VISIBLE);

            adapter = new OrdersAdapter(this, orders, item -> {
                // Xử lý click xem chi tiết
            });

            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(adapter);
        } else {
            // ✅ Hiển thị text trống
            rvOrders.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}