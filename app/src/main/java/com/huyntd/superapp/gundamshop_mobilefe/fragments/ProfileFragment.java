package com.huyntd.superapp.gundamshop_mobilefe.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.activities.OrderHistoryActivity;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

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

        LinearLayout orderHistoryLL = view.findViewById(R.id.orderHistoryLL);

        orderHistoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
            }
        });

        // Lấy button từ view
//        Button btnClick = view.findViewById(R.id.button);
//
//        // Gán sự kiện click
//        btnClick.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Button clicked!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
//        });

        return view;
    }
}