package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationBarView;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityMainBinding;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ChatsListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.FavoriteListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ProductListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ProfileFragment;
import com.huyntd.superapp.gundamshop_mobilefe.models.CartItem;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.CartItemResponse;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.CartViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.OrderViewModel;

public class MainActivity extends AppCompatActivity {
    //View binding
    private ActivityMainBinding binding;

    CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //Chú ý dòng này!!!!! nếu dùng binding
        //setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        Log.d("MainActivity", "App started");
        requestNotificationPermission();
//        checkPendingPayments();

        // Cho phép layout phủ dưới status bar (fix cho Pixel, Android 12+)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_red)); // hoặc mã hex

        // --- Tránh vùng camera (notch) ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        if (!SessionManager.getInstance(MainActivity.this).isLoggedIn()) {
            startLoginOptionsActivity();
        } else {
            // Trường hợp tắt app mà chưa logout thì sessionManager vẫn lưu token tuy nhiên ApiClient đã xóa token
            // --> khi mà vào lại app ---> vào thẳng Home ko thông qua login (do sessionManager đã có token)
            // Mà ApiClient chỉ được gán token thông qua login --> bị lỗi 1 số api cần bearer token
            ApiClient.setToken(SessionManager.getInstance(MainActivity.this).getAuthToken());
        }

        // show default
        showProductListFragment();

        System.out.println("Start hereeeee");

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.nav_home){
                    System.out.println("Home hereeeee");
                    showProductListFragment();
                } else if (itemId == R.id.nav_map) {
//                    startChatActivity();
                } else if (itemId == R.id.nav_notification) {
                    System.out.println("Notification here");
                    showFavoriteListFragment();
                } else if (itemId == R.id.nav_profile) {
                    System.out.println("Profile hereeeee");
                    showProfileFragment();
                }
                return true;
            }
        });
    }

    private void showProductListFragment() {
        // Kiểm tra nếu fragment đã tồn tại, không cần tạo lại
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag("ProductListFragment");

        if (existingFragment == null) {
            ProductListFragment productListFragment = new ProductListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentsFL.getId(), productListFragment, "ProductListFragment")
                    .commit();
        } else {
            // Nếu fragment đã có (VD: xoay màn hình), chỉ cần hiển thị lại
            getSupportFragmentManager().beginTransaction()
                    .show(existingFragment)
                    .commit();
        }
    }

    private void showChatsListFragment() {

//        binding.toolbarTitleTv.setText("Chats");

        ChatsListFragment chatsListFragment = new ChatsListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), chatsListFragment, "ChatsListFragment");
        fragmentTransaction.commit();

    }

    private void showFavoriteListFragment() {

//        binding.toolbarTitleTv.setText("Favorites");

        FavoriteListFragment favoriteListFragment = new FavoriteListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), favoriteListFragment, "FavoriteListFragment");
        fragmentTransaction.commit();

    }
    private void showProfileFragment() {

//        binding.toolbarTitleTv.setText("Profile");

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), profileFragment, "ProfileFragment");
        fragmentTransaction.commit();

    }

    private void handleShowChat() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        String userRole = sessionManager.getRole();
        if("STAFF".equalsIgnoreCase(userRole)){
            showChatsListFragment();
        } else if ("CUSTOMER".equalsIgnoreCase(userRole)) {
//            show
        }
    }

    private void startLoginOptionsActivity() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }

    //----------------------Notification cart -------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        checkPendingPayments();
    }

    private void checkPendingPayments() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        String userId = sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            Log.d("MainActivity", "User chưa đăng nhập");
            return;
        }

        cartViewModel.getCartsByUserId(Integer.parseInt(userId)).observe(this, cartResponse -> {
            if (cartResponse != null && cartResponse.getItems() != null) {
                int pendingCount = cartResponse.getItems().size();

                if (pendingCount > 0) {
                    // ✅ Hiển thị thông báo trong app
//                    showPaymentAlert(pendingCount);

                    // ✅ Hiển thị thông báo ngoài app (badge notification)
                    showCartBadgeNotification(pendingCount);
                } else {
                    Log.d("MainActivity", "Giỏ hàng trống");
                }
            } else {
                Log.d("MainActivity", "Giỏ hàng null");
            }
        });
    }


//    private void showPaymentAlert(int count) {
//        new AlertDialog.Builder(this)
//                .setTitle("Thông báo")
//                .setMessage("Bạn có " + count + " sản phẩm đang chờ thanh toán.")
//                .setPositiveButton("Xem ngay", (dialog, which) -> {
//                    // Chuyển sang màn hình giỏ hàng hoặc thanh toán
//                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
//                    startActivity(intent);
//                })
//                .setNegativeButton("Đóng", null)
//                .show();
//    }

    private void showCartBadgeNotification(int cartCount) {
        String channelId = "cart_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Cart Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("Giỏ hàng của bạn")
                .setContentText("Bạn có " + cartCount + " sản phẩm trong giỏ hàng.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setNumber(cartCount)
                .setAutoCancel(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // ✅ Kiểm tra quyền trước khi gửi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build());
        } else {
            Log.e("Notification", "Chưa được cấp quyền POST_NOTIFICATIONS!");
        }
    }


    //Xin quyền gửi notifi
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    //---------------------------------------------------------------------------------------------------


    private void startChatActivity() {
        startActivity(new Intent(this, ChatActivity.class));
    }

}