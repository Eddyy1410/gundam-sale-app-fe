package com.huyntd.superapp.gundamshop_mobilefe.fragments.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.FragmentDashboardBinding;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.DashboardViewModel;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getTodayOrders().observe(getViewLifecycleOwner(),
                count -> binding.tvTodayOrders.setText("Tổng đơn hôm nay: " + count));
        viewModel.getPendingOrders().observe(getViewLifecycleOwner(),
                count -> binding.tvPendingOrders.setText("Đơn đang chờ xử lý: " + count));
        viewModel.getLowStock().observe(getViewLifecycleOwner(),
                count -> binding.tvLowStock.setText("Sản phẩm sắp hết: " + count));

        viewModel.loadDashboardData();

        binding.btnViewDetails.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mở chi tiết Dashboard", Toast.LENGTH_SHORT).show()
        );

        return binding.getRoot();
    }
}
