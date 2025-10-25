package com.huyntd.superapp.gundamshop_mobilefe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.PageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.AuthenticationRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserRegisterRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderItemResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.OrderResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import retrofit2.Call;


public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {

    private Context context;
    private List<OrderResponse> list;
    private OnItemClickListener listener;
    private OrderViewModel orderViewModel; // không new ở đây

    public interface OnItemClickListener {
        void onDetailClick(OrderResponse item);
    }

    public OrdersAdapter(Context context, List<OrderResponse> list,
                         OnItemClickListener listener, OrderViewModel orderViewModel) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.orderViewModel = orderViewModel;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderResponse item = list.get(position);
        var orderResponse = orderViewModel.getOrderDetail(list.get(position).getId());
        holder.tvName.setText(item.getOrderItems().get(0).getProductName());
        holder.tvDate.setText(item.getOrderDate()+"");
        holder.tvStatus.setText(item.getStatus());
        holder.tvPrice.setText(item.getTotalPrice()+"");
        var img = item.getOrderItems().get(0).getProductImage();
        if (img != null && !img.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(img)
                    .into(holder.img);
        }

        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) listener.onDetailClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvDate, tvStatus, tvPrice;
        Button btnInvoice, btnDetail;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }
    }
}

