package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.ProductImageAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.RelatedProductAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ProductResponse;
import com.huyntd.superapp.gundamshop_mobilefe.ui.theme.GridSpacingItemDecoration;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.CartViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.ProductDetailViewModel;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductDetailViewModel viewModel;
    private CartViewModel cartViewModel;
    private SessionManager sessionManager;
    private Handler autoScrollHandler = new Handler();
    private Runnable autoScrollRunnable;

    private ViewPager2 vpProductImages;
    private ImageView ivBack, ivCart;
    private TextView tvProductName, tvProductPrice, tvProductQuantity, tvProductDescription;
    private Button btnBuy;
    private Button btnAddToCart;
    private View navbar;
    private ScrollView scrollView;

    private RecyclerView rvRelated;
    private RelatedProductAdapter relatedProductAdapter;

    private List<String> imageUrls;
    private ProductResponse currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        setupScrollEffect();
        setupButtons();

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        sessionManager = SessionManager.getInstance(this);

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            observeData();
            viewModel.loadProductDetail(productId);
        } else {
            Toast.makeText(this, "Thiếu product_id", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        vpProductImages = findViewById(R.id.vpProductImages);
        ivBack = findViewById(R.id.ivBack);
        ivCart = findViewById(R.id.ivCart);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductQuantity = findViewById(R.id.tvProductQuantity);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        btnBuy = findViewById(R.id.btnBuy);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        navbar = findViewById(R.id.navbar);
        scrollView = findViewById(R.id.scrollView);
        rvRelated = findViewById(R.id.rvRelatedProducts);
        relatedProductAdapter = new RelatedProductAdapter();
        rvRelated.setAdapter(relatedProductAdapter);

        relatedProductAdapter.setOnItemClickListener(product -> {
            if (product == null) return;

            Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

// Dạng lưới 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvRelated.setLayoutManager(gridLayoutManager);

// Thêm khoảng cách giữa các item
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvRelated.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
    }

    private void observeData() {
        viewModel.getProductLiveData().observe(this, product -> {
            if (product != null){
                currentProduct = product;
                bindData(product);
                loadRelatedProducts(product.getCategoryId());
            }
            else Toast.makeText(this, "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();

        });

    }

    private void bindData(ProductResponse product) {
        tvProductName.setText(product.getName());
        tvProductPrice.setText(String.format("%,.0f₫", product.getPrice().doubleValue()));
        tvProductQuantity.setText("Số lượng: " + product.getQuantity());
        tvProductDescription.setText(product.getFullDescription() != null ? product.getFullDescription() : "Không có mô tả");

        btnBuy.setText(String.format("Mua với giá %,.0f₫", product.getPrice().doubleValue()));

        imageUrls = product.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            ProductImageAdapter adapter = new ProductImageAdapter(imageUrls);
            vpProductImages.setAdapter(adapter);
            setupAutoScroll();
        }
    }

    private void setupButtons() {
        ivBack.setOnClickListener(v -> finish());
        ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
        btnBuy.setOnClickListener(v -> addToCart());
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentProduct == null) {
            Toast.makeText(this, "Không thể thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String userIdStr = sessionManager.getUserId();
        if (userIdStr == null) {
            Toast.makeText(this, "Lỗi lấy thông tin user", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            int productId = currentProduct.getId();

            cartViewModel.addToCart(userId, productId).observe(this, success -> {
                if (success != null && success) {
                    Toast.makeText(this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không thể thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lỗi định dạng user ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupScrollEffect() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = scrollView.getScrollY();
            if (scrollY > 200) {
                navbar.setBackgroundColor(Color.WHITE);
            } else {
                navbar.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    private void setupAutoScroll() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (imageUrls == null || imageUrls.isEmpty()) return;
                int next = (vpProductImages.getCurrentItem() + 1) % imageUrls.size();
                vpProductImages.setCurrentItem(next, true);
                autoScrollHandler.postDelayed(this, 3000);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
    }

    private void loadRelatedProducts(int categoryId) {
        viewModel.loadRelatedProducts(categoryId).observe(this, products -> {
            Log.d("ProductDetail", "Related observer triggered: " + (products != null ? products.size() : "null"));

            if (relatedProductAdapter == null) {
                Log.e("ProductDetail", "⚠️ relatedAdapter is NULL!");
                return;
            }

            if (products != null) {
                relatedProductAdapter.setData(products);
            } else {
                Toast.makeText(this, "Không tải được sản phẩm liên quan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoScrollHandler != null && autoScrollRunnable != null)
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }
}
