package com.huyntd.superapp.gundamshop_mobilefe.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.activities.OrderHistoryActivity;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.OrdersAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.UserViewModel;

public class ProfileFragment extends Fragment {

    TextView tvOrdersCount, tvName, tvPhone, tvPointsCount;
    ImageView imgAvatar;

    OrderViewModel orderViewModel;
    UserViewModel userViewModel;

    int userId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout và gán vào biến view
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ✅ Đảm bảo layout tránh vùng camera và status bar
        View rootView = view.findViewById(R.id.FrameLayout);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Giảm bớt top padding (chỉ giữ lại một phần nhỏ)
            int reducedTop = Math.max(systemBars.top - 120, 0); // giảm 40px hoặc tuỳ chỉnh
            v.setPadding(systemBars.left, reducedTop, systemBars.right, systemBars.bottom);

            return insets;
        });

        tvName = view.findViewById(R.id.tv_user_name);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        tvOrdersCount = view.findViewById(R.id.tv_orders_count);
        tvPointsCount = view.findViewById(R.id.tv_points_count);
        imgAvatar = view.findViewById(R.id.iv_avatar);

        // 🔹 Lấy ViewModel (chuẩn AndroidX)
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 🔹 Gọi API qua ViewModel
        //1. Api lấy thông tin người dùng
        userViewModel.getInfo().observe(getViewLifecycleOwner(), userResponse -> {
            if (userResponse != null){
                Glide.with(this)
                        .load("https://i.pinimg.com/736x/30/a8/49/30a8490ff409df33d1e23702cf2c4aa8.jpg")
                        .override(300, 300) // fix size 200x200 pixel
                        .centerCrop()       // cắt giữa hình để không méo
                        .into(imgAvatar);

                tvName.setText(userResponse.getFullName());
                tvPhone.setText(userResponse.getPhone());
                userId = userResponse.getId();

                // 2. Api lấy lịch sử mua hàng
                orderViewModel.getOrdersByUserId(userId).observe(getViewLifecycleOwner(), orders -> {
                    if (orders != null && !orders.isEmpty()) {
                        tvOrdersCount.setText(String.valueOf(orders.size()));

                        // ✅ Tính tổng tiền (nếu có field totalPrice trong OrderResponse)
                        double total = 0;
                        for (OrderResponse o : orders) {
                            if(o.getStatus().equals("DELIVERED")){
                                total += o.getTotalPrice();
                            }
                        }
                        tvPointsCount.setText(String.format("%,.0fđ", total));
                    } else {
                        Log.d("OrderHistory", "Không có đơn hàng nào.");
                    }
                });
            }
        });

        LinearLayout orderHistoryLL = view.findViewById(R.id.orderHistoryLL);

        orderHistoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
            }
        });


        return view;
    }
}