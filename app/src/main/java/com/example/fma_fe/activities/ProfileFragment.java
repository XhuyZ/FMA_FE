package com.example.fma_fe.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fma_fe.R;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Tìm CircleImageView
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);

        // Load image từ URL
        String imageUrl = "https://i.pravatar.cc/150?u=1";

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(ivProfilePicture);

        return view;
    }
}