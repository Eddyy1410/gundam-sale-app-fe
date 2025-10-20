package com.huyntd.superapp.gundamshop_mobilefe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {
    private List<ProductResponse> products = new ArrayList<>();
    private OnItemClickListener listener;
    public void setProducts(List<ProductResponse> products) {
        this.products = products;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(ProductResponse product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice().toString());

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrls().get(0))
                    .into(holder.ivProduct);
        }

        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivProduct;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
        }
    }
}
