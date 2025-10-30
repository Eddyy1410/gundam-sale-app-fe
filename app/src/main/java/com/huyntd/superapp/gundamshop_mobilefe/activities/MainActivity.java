package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Context;
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
import com.huyntd.superapp.gundamshop_mobilefe.fragments.staff.ChatsListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.FavoriteListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ProductListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ProfileFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.staff.DashboardFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.staff.QuickOrderFragment;

public class MainActivity extends AppCompatActivity {
    //View binding
    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //Chú ý dòng này!!!!! nếu dùng binding
        //setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());
        sessionManager = SessionManager.getInstance(getApplicationContext());
        // Cho phép layout phủ dưới status bar (fix cho Pixel, Android 12+)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_red)); // hoặc mã hex

        // --- Tránh vùng camera (notch) ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        sessionManager = SessionManager.getInstance(MainActivity.this);
        if (!sessionManager.isLoggedIn()) {
            startLoginOptionsActivity();
        } else {
            // Trường hợp tắt app mà chưa logout thì sessionManager vẫn lưu token tuy nhiên ApiClient đã xóa token
            // --> khi mà vào lại app ---> vào thẳng Home ko thông qua login (do sessionManager đã có token)
            // Mà ApiClient chỉ được gán token thông qua login --> bị lỗi 1 số api cần bearer token
            ApiClient.setToken(SessionManager.getInstance(MainActivity.this).getAuthToken());
            userRole = sessionManager.getRole();
        }

        System.out.println("Start hereeeee");

        setupBottomNavigationForRole(userRole);
    }

    /**
     * Chọn menu theo vai trò
     */
    private void setupBottomNavigationForRole(String role) {
        binding.bottomNavigation.getMenu().clear();

        if ("STAFF".equalsIgnoreCase(role)) {
            binding.bottomNavigation.inflateMenu(R.menu.menu_staff_bottom);
            showDashboardFragment(); // mặc định staff mở Dashboard
            setupStaffNavigation();
        } else {
            binding.bottomNavigation.inflateMenu(R.menu.menu_bottom);
            showProductListFragment(); // mặc định customer mở danh sách sản phẩm
            setupCustomerNavigation();
        }
    }

    /**
     * Xử lý navigation cho STAFF
     */
    private void setupStaffNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    showDashboardFragment();
                    return true;
                } else if (id == R.id.nav_orders) {
                    showQuickOrderFragment();
                    return true;
                } else if (id == R.id.nav_chat) {
                    showChatsListFragment();
                    return true;
                }
//                } else if (id == R.id.nav_search) {
//                    showProductSearchFragment();
//                    return true;
//                } else if (id == R.id.nav_notify) {
//                    showNotificationsFragment();
//                    return true;
//                }
                return false;
            }
        });
    }


    /**
     * Xử lý navigation cho CUSTOMER (logic cũ giữ nguyên)
     */
    private void setupCustomerNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    showProductListFragment();
                } else if (id == R.id.nav_map) {
                    // TODO: Thêm chức năng Cửa hàng
                } else if (id == R.id.nav_notification) {
                    showFavoriteListFragment(); // hoặc màn hình thông báo
                } else if (id == R.id.nav_profile) {
                    showProfileFragment();
                }
                return true;
            }
        });
    }

    // ----------------- CUSTOMER FRAGMENTS -----------------
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

//    private void handleShowChat() {
//        SessionManager sessionManager = SessionManager.getInstance(this);
//        String userRole = sessionManager.getRole();
//        if("STAFF".equalsIgnoreCase(userRole)){
//            showChatsListFragment();
//        } else if ("CUSTOMER".equalsIgnoreCase(userRole)) {
//            show
//        }
//    }

    private void startLoginOptionsActivity() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }

    // ----------------- COMMON UTILS -----------------
    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(binding.fragmentsFL.getId(), fragment, tag);
        ft.commit();
    }

    private void startChatActivity() {
        startActivity(new Intent(this, ChatActivity.class));
    }


    // ----------------- STAFF FRAGMENTS -----------------
    private void showDashboardFragment() {
        replaceFragment(new DashboardFragment(), "DashboardFragment");
    }

    private void showQuickOrderFragment() {
        replaceFragment(new QuickOrderFragment(), "QuickOrderFragment");
    }

    private void showChatsListFragment() {
        replaceFragment(new ChatsListFragment(), "ChatsListFragment");
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

//    private void showProductSearchFragment() {
//        replaceFragment(new ProductSearchFragment(), "ProductSearchFragment");
//    }
//
//    private void showConversationsFragment() {
//        replaceFragment(new ConversationsFragment(), "ConversationsFragment");
//    }
//
//    private void showNotificationsFragment() {
//        replaceFragment(new NotificationsFragment(), "NotificationsFragment");
//    }

}