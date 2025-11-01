package com.huyntd.superapp.gundamshop_mobilefe.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.activities.CartActivity;
import com.huyntd.superapp.gundamshop_mobilefe.activities.ChatActivity;
import com.huyntd.superapp.gundamshop_mobilefe.activities.ProductDetailActivity;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.ProductListAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;
import com.huyntd.superapp.gundamshop_mobilefe.ui.theme.GridSpacingItemDecoration;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.ProductListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass
 * create an instance of this fragment.
 */
public class ProductListFragment extends Fragment {

    private ProductListViewModel viewModel;
    private ProductListAdapter adapter;
    private List<ProductResponse> allProducts = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        // Ánh xạ view
        RecyclerView rvProducts = view.findViewById(R.id.rvProducts);
        EditText etSearch = view.findViewById(R.id.etSearch);
        ImageView ivCart = view.findViewById(R.id.ivCart);
        ImageView ivChat = view.findViewById(R.id.ivChat);

        // Setup RecyclerView
        adapter = new ProductListAdapter();

        //thêm listener
        adapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        rvProducts.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                allProducts.clear();
                allProducts.addAll(products);
                adapter.setProducts(products);
                Log.d("ProductListFragment", "API returned " + products.size() + " products");
                for (ProductResponse p : products) {
                    Log.d("ProductListFragment", "Product: " + p.getName() + " - " + p.getPrice());
                }
            } else{
                Log.d("ProductListFragment", "API returned null");
            }
        });

        // Search listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Cart click listener
        ivCart.setOnClickListener(v ->
                        // TODO: mở màn hình giỏ hàng (ví dụ CartActivity)
                         startActivity(new Intent(requireContext(), CartActivity.class))
        );



        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChatActivity.class));
            }
        });

        return view;
    }

    private void filterProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setProducts(allProducts);
            return;
        }

        List<ProductResponse> filtered = new ArrayList<>();
        for (ProductResponse p : allProducts) {
            if (p.getName() != null &&
                    p.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.setProducts(filtered);
    }
}