package com.huyntd.superapp.gundamshop_mobilefe.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.activities.MainActivity;
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

        // Lấy button từ view
        Button btnClick = view.findViewById(R.id.button);

        // Gán sự kiện click
        btnClick.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Button clicked!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
        });

        return view;
    }
}