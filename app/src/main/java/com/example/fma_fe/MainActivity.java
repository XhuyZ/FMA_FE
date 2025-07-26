package com.example.fma_fe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fma_fe.R;
import com.example.fma_fe.activities.ChatActivity;
import com.example.fma_fe.activities.ContactFragment;
import com.example.fma_fe.activities.CreatePostActivity;
import com.example.fma_fe.activities.HomeFragment;
import com.example.fma_fe.activities.MatchFragment;
import com.example.fma_fe.activities.ProfileFragment;
import com.example.fma_fe.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("huy","enter oncreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        // Setup bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.match) {
                replaceFragment(new MatchFragment());
            } else if (itemId == R.id.contact) {
                replaceFragment(new ContactFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });

        // Setup FAB click listener
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(v -> {
            Log.d("MainActivity", "FAB clicked - opening CreatePostActivity");
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        // FAB chat hỗ trợ
        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        Log.d("huy","exit oncreate");
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the current fragment when returning from CreatePostActivity
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof HomeFragment) {
            // Refresh the home fragment to show new posts
            replaceFragment(new HomeFragment());
        }
    }
}