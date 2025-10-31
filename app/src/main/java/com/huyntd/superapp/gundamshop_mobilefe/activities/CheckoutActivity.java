package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.CheckoutAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.CreateOrderRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.OrderItemRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.PaymentRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.CartItemResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.CartViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.PaymentViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.UserViewModel;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    TextView tvReceiverName;
    TextView tvReceiverPhone;
    EditText etReceiverAddress;
    TextView tvQuantity;
    TextView tvSubtotal;
    TextView tvTotal;
    TextView tvBottomTotal;
    ImageButton btnBack;
    Button btnPay;
    RecyclerView rvProducts;
    CheckoutAdapter adapter;
    UserViewModel userViewModel;
    CartViewModel cartViewModel;
    OrderViewModel orderViewModel;
    PaymentViewModel paymentViewModel;

    RadioGroup rgPayment;
    RadioButton rbStore;
    RadioButton rbMomo;
    RadioButton rbVnpay;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Ánh xạ dữ  liệu
        mappingViews();

        // 🔹 Lấy ViewModel (chuẩn AndroidX)
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        // 🔹 Gọi API qua ViewModel
        //1. Api lấy thông tin người dùng
        userViewModel.getInfo().observe(this, userResponse -> {
            if (userResponse != null) {
                tvReceiverName.setText(userResponse.getFullName());
                tvReceiverPhone.setText(userResponse.getPhone());
                userId = userResponse.getId();

                //2. Api lấy thông tin sản phẩm trong cart người dùng
                var cart = cartViewModel.getCartsByUserId(userId);
                cartViewModel.getCartsByUserId(userId).observe(this, cartResponse -> {
                    if (cartResponse != null) {
                        adapter = new CheckoutAdapter(
                                this,
                                cartResponse.getItems()
                        );

                        rvProducts.setLayoutManager(new LinearLayoutManager(this));
                        rvProducts.setAdapter(adapter);

                        int quantity = 0;
                        double total = 0;
                        var itemList = new ArrayList<OrderItemRequest>();
                        for (CartItemResponse o : cartResponse.getItems()) {
                            total += o.getQuantity() * o.getProductPrice(); // nhớ dùng += để cộng dồn
                            quantity += o.getQuantity();
                            itemList.add(new OrderItemRequest(o.getProductId(), o.getQuantity()));
                        }
                        tvQuantity.setText(String.valueOf(quantity));
                        tvSubtotal.setText(String.format("%,.0fđ", total)); // vẫn được
                        tvTotal.setText(String.format("%,.0fđ", total));
                        tvBottomTotal.setText(String.format("%,.0fđ", total));

                        //Lấy tổng tiền cuối
                        double finalTotal = total;
                        btnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int selectedId = rgPayment.getCheckedRadioButtonId();
                                if (selectedId == -1) {
                                    Toast.makeText(CheckoutActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String paymentMethod = selectedId == R.id.rbStore ? "COD"
                                        : selectedId == R.id.rbMomo ? "MOMO"
                                        : "VNPAY";

                                CreateOrderRequest request = new CreateOrderRequest();
                                request.setUserId(userId);
                                request.setBillingAddress(etReceiverAddress.getText().toString());
                                request.setPaymentMethod(paymentMethod);
                                request.setTotalPrice(finalTotal);
                                request.setOrderItems(itemList); // danh sách Cart -> OrderItemRequest
                                handleOrderCreation(request, paymentMethod);
                            }
                        });
                    } else {
                        Log.d("OrderHistory", "Không có đơn hàng nào.");
                    }
                });


            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void mappingViews() {
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        etReceiverAddress = findViewById(R.id.etReceiverAddress);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        tvBottomTotal = findViewById(R.id.tvBottomTotal);
        rvProducts = findViewById(R.id.rvProducts);
        btnBack = findViewById(R.id.btn_back);
        btnPay = findViewById(R.id.btnPay);
        rgPayment = findViewById(R.id.rgPayment);
        rbStore = findViewById(R.id.rbStore);
        rbMomo = findViewById(R.id.rbMomo);
        rbVnpay = findViewById(R.id.rbVnpay);
    }

    private void handleOrderCreation(CreateOrderRequest request, String paymentMethod) {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.createNewOrder(request).observe(this, orderResponse -> {
            if (orderResponse != null) {
                Toast.makeText(this, "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                var payment = new PaymentRequest(orderResponse.getId(), orderResponse.getTotalPrice());
                if (paymentMethod.equals("VNPAY")) {
                    handlePayWithVNPAY(payment);
                } else if (paymentMethod.equals("MOMO")){
                    handlePayWithMomo(payment);
                } else {
                    Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, PaymentSuccessActivity.class);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(this, "Tạo đơn hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePayWithVNPAY(PaymentRequest request) {
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        paymentViewModel.createVNPAYPayment(request).observe(this, paymentUrl -> {
            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                // Mở trình duyệt hoặc WebView để thanh toán
                Intent intent = new Intent(this, PaymentWebViewActivity.class);
                intent.putExtra("paymentUrl", paymentUrl);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Tạo thanh toán VNPAY thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePayWithMomo(PaymentRequest request) {
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        paymentViewModel.createMomoPayment(request).observe(this, paymentUrl -> {
            if (paymentUrl != null && !paymentUrl.isEmpty()) {
                // Mở trình duyệt hoặc WebView để thanh toán
                Intent intent = new Intent(this, PaymentWebViewActivity.class);
                intent.putExtra("paymentUrl", paymentUrl);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Tạo thanh toán MOMO thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

}