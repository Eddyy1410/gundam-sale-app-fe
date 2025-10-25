package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityMainBinding;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.ChatsListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.FavoriteListFragment;
import com.huyntd.superapp.gundamshop_mobilefe.fragments.HomeFragment;
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

        // --- Tránh vùng camera (notch) ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        if (!SessionManager.getInstance(MainActivity.this).isLoggedIn()) {
            startLoginOptionsActivity();
        }

        // show default
        showProductListFragment();

        System.out.println("Start hereeeee");

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.item_home){
                    System.out.println("Home hereeeee");
                    showProductListFragment();
                } else if (itemId == R.id.item_chats) {
                    startChatActivity();
                } else if (itemId == R.id.item_favorite) {
                    System.out.println("Fav hereeeee");
                    showFavoriteListFragment();
                } else if (itemId == R.id.item_profile) {
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