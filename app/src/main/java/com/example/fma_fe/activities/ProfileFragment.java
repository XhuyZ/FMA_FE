package com.example.fma_fe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fma_fe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // UI Components
    private ImageView ivProfilePicture;
    private TextView tvUserName;
    private TextView tvUserPosition;
    private TextView tvUserAge;
    private TextView tvUserPhone;
    private TextView tvUserBio;
    private TextView tvUserEmail;
    private TextView tvUserTeam;
    private TextView tvUserId;
    private Button btnLogout;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        initializeViews(view);

        // Setup click listeners
        setupClickListeners();

        // Load user data
        loadUserProfile();

        return view;
    }

    private void initializeViews(View view) {
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserPosition = view.findViewById(R.id.tv_user_position);
        tvUserAge = view.findViewById(R.id.tv_user_age);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvUserBio = view.findViewById(R.id.tv_user_bio);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserTeam = view.findViewById(R.id.tv_user_team);
        tvUserId = view.findViewById(R.id.tv_user_id);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        // Logout button
        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Show logout message
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate to SignInActivity
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Close current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();

            // Show user ID for debugging (remove in production)
            tvUserId.setText("ID: " + currentUserId);

            // Load user data from Firebase Realtime Database
            DatabaseReference userRef = mDatabase.child("users").child(currentUserId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            // Parse data manually to handle type conversion
                            UserProfile userProfile = parseUserProfile(dataSnapshot);
                            updateUI(userProfile);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing user profile: " + e.getMessage());
                            showError("Failed to parse user data");
                        }
                    } else {
                        Log.e(TAG, "User data does not exist");
                        showError("User data not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    showError("Failed to load user data: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e(TAG, "No authenticated user");
            showError("Please login first");
        }
    }

    private UserProfile parseUserProfile(DataSnapshot dataSnapshot) {
        UserProfile userProfile = new UserProfile();

        // Parse each field with proper type handling
        if (dataSnapshot.child("age").exists()) {
            Object ageValue = dataSnapshot.child("age").getValue();
            if (ageValue instanceof Long) {
                userProfile.setAge(((Long) ageValue).intValue());
            } else if (ageValue instanceof Integer) {
                userProfile.setAge((Integer) ageValue);
            }
        }

        if (dataSnapshot.child("bio").exists()) {
            userProfile.setBio(dataSnapshot.child("bio").getValue(String.class));
        }

        if (dataSnapshot.child("email").exists()) {
            userProfile.setEmail(dataSnapshot.child("email").getValue(String.class));
        }

        if (dataSnapshot.child("imageUrl").exists()) {
            userProfile.setImageUrl(dataSnapshot.child("imageUrl").getValue(String.class));
        }

        if (dataSnapshot.child("name").exists()) {
            userProfile.setName(dataSnapshot.child("name").getValue(String.class));
        }

        if (dataSnapshot.child("phone").exists()) {
            Object phoneValue = dataSnapshot.child("phone").getValue();
            if (phoneValue instanceof Long) {
                userProfile.setPhone(String.valueOf(phoneValue));
            } else if (phoneValue instanceof String) {
                userProfile.setPhone((String) phoneValue);
            }
        }

        if (dataSnapshot.child("position").exists()) {
            userProfile.setPosition(dataSnapshot.child("position").getValue(String.class));
        }

        if (dataSnapshot.child("teamId").exists()) {
            userProfile.setTeamId(dataSnapshot.child("teamId").getValue(String.class));
        }

        if (dataSnapshot.child("userId").exists()) {
            Object userIdValue = dataSnapshot.child("userId").getValue();
            if (userIdValue instanceof Long) {
                userProfile.setUserId(((Long) userIdValue).intValue());
            } else if (userIdValue instanceof Integer) {
                userProfile.setUserId((Integer) userIdValue);
            }
        }

        return userProfile;
    }

    private void updateUI(UserProfile userProfile) {
        // Update user name
        tvUserName.setText(userProfile.getName() != null ? userProfile.getName() : "No Name");

        // Update user position
        tvUserPosition.setText(userProfile.getPosition() != null ? userProfile.getPosition() : "No Position");

        // Update user age
        tvUserAge.setText("Age: " + (userProfile.getAge() != null ? userProfile.getAge() : "N/A"));

        // Update user phone
        tvUserPhone.setText("📞 " + (userProfile.getPhone() != null ? userProfile.getPhone() : "No Phone"));

        // Update user bio
        tvUserBio.setText(userProfile.getBio() != null ? userProfile.getBio() : "No bio available");

        // Update user email
        tvUserEmail.setText(userProfile.getEmail() != null ? userProfile.getEmail() : "No Email");

        // Update user team
        tvUserTeam.setText(userProfile.getTeamId() != null ? userProfile.getTeamId() : "No Team");

        // Load profile picture
        loadProfilePicture(userProfile.getImageUrl());
    }

    private void loadProfilePicture(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_placeholder)
                    .into(ivProfilePicture);
        } else {
            // Use default placeholder
            ivProfilePicture.setImageResource(R.drawable.ic_user_placeholder);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    // User Profile model class
    public static class UserProfile {
        private Integer age;
        private String bio;
        private String email;
        private String imageUrl;
        private String name;
        private String phone;
        private String position;
        private String teamId;
        private Integer userId;

        public UserProfile() {
            // Default constructor required for Firebase
        }

        // Getters
        public Integer getAge() {
            return age;
        }

        public String getBio() {
            return bio;
        }

        public String getEmail() {
            return email;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getPosition() {
            return position;
        }

        public String getTeamId() {
            return teamId;
        }

        public Integer getUserId() {
            return userId;
        }

        // Setters
        public void setAge(Integer age) {
            this.age = age;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }
    }
}