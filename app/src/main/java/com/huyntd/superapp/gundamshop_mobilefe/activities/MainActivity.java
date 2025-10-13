package com.huyntd.superapp.gundamshopmobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.huyntd.superapp.gundamshopmobilefe.R;
import com.huyntd.superapp.gundamshopmobilefe.databinding.ActivityMainBinding;
import com.huyntd.superapp.gundamshopmobilefe.fragments.ChatsListFragment;
import com.huyntd.superapp.gundamshopmobilefe.fragments.FavoriteListFragment;
import com.huyntd.superapp.gundamshopmobilefe.fragments.HomeFragment;
import com.huyntd.superapp.gundamshopmobilefe.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    //View binding
    private ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml = ActivityMainBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //Chú ý dòng này!!!!! nếu dùng binding
        //setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());

        firebaseAuth = firebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startLoginOptionsActivity();
        }

        // show default
        showHomeFragment();

        System.out.println("Start hereeeee");

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.item_home){
                    System.out.println("Home hereeeee");
                    showHomeFragment();
                } else if (itemId == R.id.item_chats) {
                    System.out.println("Chats hereeeee");
                    showChatsListFragment();
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

    private void showHomeFragment() {

        binding.toolbarTitleTv.setText("Home");

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // dùng fragment "homeFragment" bỏ vào fragment layout "fragmentsFL"
        // gán tag để sau này có thể dùng getSupportFragmentManager().findFragmentByTag("HomeFragment")
        fragmentTransaction.replace(binding.fragmentsFL.getId(), homeFragment, "HomeFragment");
        fragmentTransaction.commit();

    }

    private void showChatsListFragment() {

        binding.toolbarTitleTv.setText("Chats");

        ChatsListFragment chatsListFragment = new ChatsListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), chatsListFragment, "ChatsListFragment");
        fragmentTransaction.commit();

    }

    private void showFavoriteListFragment() {

        binding.toolbarTitleTv.setText("Favorites");

        FavoriteListFragment favoriteListFragment = new FavoriteListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), favoriteListFragment, "FavoriteListFragment");
        fragmentTransaction.commit();

    }
    private void showProfileFragment() {

        binding.toolbarTitleTv.setText("Profile");

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFL.getId(), profileFragment, "ProfileFragment");
        fragmentTransaction.commit();

    }

    private void startLoginOptionsActivity() {
        startActivity(new Intent(this, LoginOptionsActivity.class));
    }

}