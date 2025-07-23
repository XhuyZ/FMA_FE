package com.example.fma_fe.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fma_fe.MainActivity;
import com.example.fma_fe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText etDescription;
    private Spinner spinnerLookingFor;
    private Spinner spinnerPitch;
    private EditText etMatchDateTime;
    private ImageView ivPostImage;
    private Button btnSelectImage;
    private Button btnCreatePost;
    private Button btnCancel;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String currentUserId;
    private Integer currentUserIdInt;
    private Integer currentTeamId;

    // Data
    private Uri selectedImageUri;
    private Calendar selectedDateTime;
    private List<PitchItem> pitchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Initialize Firebase with proper configuration
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        // Initialize Storage with proper bucket configuration
        try {
            mStorage = FirebaseStorage.getInstance("gs://your-project-bucket-name").getReference();
        } catch (Exception e) {
            // Fallback to default storage
            mStorage = FirebaseStorage.getInstance().getReference();
            Log.w(TAG, "Using default storage bucket", e);
        }

        initializeViews();
        setupSpinners();
        setupClickListeners();
        loadCurrentUserData();
        loadPitches();
    }

    private void initializeViews() {
        etDescription = findViewById(R.id.et_description);
        spinnerLookingFor = findViewById(R.id.spinner_looking_for);
        spinnerPitch = findViewById(R.id.spinner_pitch);
        etMatchDateTime = findViewById(R.id.et_match_date_time);
        ivPostImage = findViewById(R.id.iv_post_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnCreatePost = findViewById(R.id.btn_create_post);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);

        // Make date time field non-editable (only clickable)
        etMatchDateTime.setFocusable(false);
        etMatchDateTime.setClickable(true);
    }

    private void setupSpinners() {
        // Setup Looking For spinner
        String[] lookingForOptions = {"Opponent", "Teammate"};
        ArrayAdapter<String> lookingForAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, lookingForOptions);
        lookingForAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLookingFor.setAdapter(lookingForAdapter);
    }

    private void setupClickListeners() {
        // Date Time picker
        etMatchDateTime.setOnClickListener(v -> showDateTimePicker());

        // Image selection
        btnSelectImage.setOnClickListener(v -> selectImage());

        // Create post
        btnCreatePost.setOnClickListener(v -> createPost());

        // Cancel
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute);

                                // Format and display the selected date time
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                etMatchDateTime.setText(sdf.format(selectedDateTime.getTime()));
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Display selected image
            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(ivPostImage);

            ivPostImage.setVisibility(View.VISIBLE);
        }
    }

    private void loadCurrentUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();

            mDatabase.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get user ID
                        Object userIdValue = dataSnapshot.child("userId").getValue();
                        if (userIdValue instanceof Long) {
                            currentUserIdInt = ((Long) userIdValue).intValue();
                        } else if (userIdValue instanceof Integer) {
                            currentUserIdInt = (Integer) userIdValue;
                        }

                        // Get team ID
                        String teamIdStr = dataSnapshot.child("teamId").getValue(String.class);
                        if (teamIdStr != null && !teamIdStr.isEmpty()) {
                            try {
                                // Extract number from team_1, team_2, etc.
                                String numStr = teamIdStr.replace("team_", "");
                                currentTeamId = Integer.parseInt(numStr);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Error parsing team ID: " + e.getMessage());
                                currentTeamId = 1; // Default value
                            }
                        } else {
                            currentTeamId = 1; // Default value
                        }

                        Log.d(TAG, "Current User ID: " + currentUserIdInt + ", Team ID: " + currentTeamId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to load user data: " + databaseError.getMessage());
                }
            });
        }
    }

    private void loadPitches() {
        mDatabase.child("pitches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pitchList.clear();

                for (DataSnapshot pitchSnapshot : dataSnapshot.getChildren()) {
                    String pitchId = pitchSnapshot.getKey();
                    String pitchName = pitchSnapshot.child("name").getValue(String.class);

                    if (pitchId != null && pitchName != null) {
                        pitchList.add(new PitchItem(pitchId, pitchName));
                    }
                }

                // Setup pitch spinner
                PitchAdapter pitchAdapter = new PitchAdapter(CreatePostActivity.this, pitchList);
                spinnerPitch.setAdapter(pitchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load pitches: " + databaseError.getMessage());
                Toast.makeText(CreatePostActivity.this, "Failed to load pitches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPost() {
        if (!validateInputs()) {
            return;
        }

        showLoading(true);

        if (selectedImageUri != null) {
            uploadImageAndCreatePost();
        } else {
            createPostWithoutImage();
        }
    }

    private boolean validateInputs() {
        String description = etDescription.getText().toString().trim();
        String matchDateTime = etMatchDateTime.getText().toString().trim();

        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return false;
        }

        if (matchDateTime.isEmpty()) {
            Toast.makeText(this, "Please select match date and time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentUserIdInt == null || currentTeamId == null) {
            Toast.makeText(this, "User data not loaded yet. Please try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pitchList.isEmpty()) {
            Toast.makeText(this, "Pitches not loaded yet. Please try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadImageAndCreatePost() {
        // Create a unique filename using UUID and timestamp
        String fileExtension = getFileExtension(selectedImageUri);
        String fileName = "post_images/" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "." + fileExtension;

        StorageReference imageRef = mStorage.child(fileName);

        Log.d(TAG, "Uploading image to path: " + fileName);

        imageRef.putFile(selectedImageUri)
                .addOnProgressListener(taskSnapshot -> {
                    // Optional: Show upload progress
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "Upload progress: " + progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Image uploaded successfully");
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d(TAG, "Download URL obtained: " + imageUrl);
                        createPostWithImage(imageUrl);
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL: " + e.getMessage(), e);
                        showLoading(false);
                        Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image: " + e.getMessage(), e);
                    showLoading(false);

                    // Provide more specific error messages
                    String errorMessage = "Failed to upload image";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("404")) {
                            errorMessage = "Storage bucket not found. Please check Firebase configuration.";
                        } else if (e.getMessage().contains("403")) {
                            errorMessage = "Permission denied. Please check Firebase Storage rules.";
                        } else if (e.getMessage().contains("network")) {
                            errorMessage = "Network error. Please check your internet connection.";
                        } else {
                            errorMessage = "Upload failed: " + e.getMessage();
                        }
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

                    // Option to continue without image
                    showContinueWithoutImageDialog();
                });
    }

    private void showContinueWithoutImageDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Upload Failed")
                .setMessage("Would you like to create the post without an image?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    selectedImageUri = null;
                    ivPostImage.setVisibility(View.GONE);
                    createPostWithoutImage();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    showLoading(false);
                })
                .show();
    }

    private String getFileExtension(Uri uri) {
        String extension = "jpg"; // default
        try {
            String fileName = uri.getLastPathSegment();
            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not determine file extension", e);
        }
        return extension;
    }

    private void createPostWithImage(String imageUrl) {
        createPostInDatabase(imageUrl);
    }

    private void createPostWithoutImage() {
        createPostInDatabase(null);
    }

    private void createPostInDatabase(String imageUrl) {
        // Get next post ID
        mDatabase.child("matchposts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nextPostId = 1;

                // Find the highest post ID and add 1
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String postKey = postSnapshot.getKey();
                        if (postKey != null && postKey.startsWith("post_")) {
                            int postId = Integer.parseInt(postKey.replace("post_", ""));
                            if (postId >= nextPostId) {
                                nextPostId = postId + 1;
                            }
                        }
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Invalid post key format: " + postSnapshot.getKey());
                    }
                }

                // Create post data
                Map<String, Object> postData = new HashMap<>();
                postData.put("postId", nextPostId);
                postData.put("teamId", currentTeamId);
                postData.put("postedByPlayerId", currentUserIdInt);

                // Get selected pitch - add null check
                if (spinnerPitch.getSelectedItem() != null) {
                    PitchItem selectedPitch = (PitchItem) spinnerPitch.getSelectedItem();
                    postData.put("pitchId", selectedPitch.getId());
                } else {
                    Log.e(TAG, "No pitch selected");
                    showLoading(false);
                    Toast.makeText(CreatePostActivity.this, "Please select a pitch", Toast.LENGTH_SHORT).show();
                    return;
                }

                postData.put("receivingTeamId", null); // Will be set when someone accepts
                postData.put("matchTime", etMatchDateTime.getText().toString().trim());
                postData.put("description", etDescription.getText().toString().trim());
                postData.put("lookingFor", spinnerLookingFor.getSelectedItem().toString());
                postData.put("postStatus", "Open");
                postData.put("imageUrl", imageUrl);

                // Current timestamp
                String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        .format(Calendar.getInstance().getTime());
                postData.put("createdAt", currentTimestamp);
                postData.put("updatedAt", currentTimestamp);

                // Save to Firebase
                String postKey = "post_" + nextPostId;
                mDatabase.child("matchposts").child(postKey).setValue(postData)
                        .addOnCompleteListener(task -> {
                            showLoading(false);
                            if (task.isSuccessful()) {
                                Toast.makeText(CreatePostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate back to MainActivity
                                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "Failed to create post: " + task.getException());
                                Toast.makeText(CreatePostActivity.this, "Failed to create post: " +
                                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showLoading(false);
                Log.e(TAG, "Failed to get post count: " + databaseError.getMessage());
                Toast.makeText(CreatePostActivity.this, "Failed to create post: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCreatePost.setEnabled(!show);
        btnSelectImage.setEnabled(!show);
    }

    // Helper classes
    public static class PitchItem {
        private String id;
        private String name;

        public PitchItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class PitchAdapter extends ArrayAdapter<PitchItem> {
        public PitchAdapter(android.content.Context context, List<PitchItem> pitches) {
            super(context, android.R.layout.simple_spinner_item, pitches);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }
}