package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityMainBinding;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ChatsListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.FavoriteListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ProductListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    //View binding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //Chú ý dòng này!!!!! nếu dùng binding
        //setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());

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
//                    showFavoriteListFragment();
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


    private void startChatActivity() {
        startActivity(new Intent(this, ChatActivity.class));
    }

}