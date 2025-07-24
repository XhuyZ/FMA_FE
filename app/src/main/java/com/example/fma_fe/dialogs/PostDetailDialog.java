package com.example.fma_fe.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import com.google.android.material.button.MaterialButton;
import com.example.fma_fe.R;
import com.example.fma_fe.models.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PostDetailDialog extends Dialog {
    private Post post;
    private OnActionClickListener listener;
    private TextView txtPitchInfo;

    public interface OnActionClickListener {
        void onContactClick(Post post);
        void onCloseClick();
    }

    public PostDetailDialog(@NonNull Context context, Post post) {
        super(context);
        this.post = post;
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_post_detail);

        // Make dialog fullscreen on small screens
        if (getWindow() != null) {
            getWindow().setLayout(
                    (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.95),
                    (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8)
            );
        }

        initializeViews();
    }

    private void initializeViews() {
        ImageView imgClose = findViewById(R.id.img_close);
        ImageView imgPost = findViewById(R.id.img_post_detail);
        TextView txtDescription = findViewById(R.id.txt_description_detail);
        TextView txtMatchTime = findViewById(R.id.txt_match_time_detail);
        txtPitchInfo = findViewById(R.id.txt_pitch_info_detail);
        TextView txtCreatedAt = findViewById(R.id.txt_created_at_detail);
        Chip chipLookingFor = findViewById(R.id.chip_looking_for_detail);
        Chip chipStatus = findViewById(R.id.chip_status_detail);
        MaterialButton btnContact = findViewById(R.id.btn_contact);

        // Set post data
        txtDescription.setText(post.getDescription());

        String lookingForText = post.getLookingFor().equals("Opponent") ?
                "Looking for Opponent" : "Looking for Teammate";
        chipLookingFor.setText(lookingForText);

        chipStatus.setText(post.getPostStatus());
        int statusColor = getStatusColor(post.getPostStatus());
        chipStatus.setChipBackgroundColorResource(statusColor);

        String formattedTime = formatDateTime(post.getMatchTime());
        txtMatchTime.setText(formattedTime);

        txtPitchInfo.setText(post.getPitchName());

        // Load image
        Glide.with(getContext())
                .load(post.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_soccer)
                        .error(R.drawable.placeholder_soccer)
                        .centerCrop()
                        .transform(new RoundedCorners(16)))
                .into(imgPost);

        // Set click listeners
        imgClose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCloseClick();
            }
            dismiss();
        });

        btnContact.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactClick(post);
            }
        });

        // Add click listener for pitch info to open Google Maps
        txtPitchInfo.setOnClickListener(v -> openGoogleMaps());

        // Enable button based on post status
        btnContact.setEnabled(post.getPostStatus().equals("Open"));
        if (!post.getPostStatus().equals("Open")) {
            btnContact.setText("Post is " + post.getPostStatus());
        }
    }

    private void openGoogleMaps() {
        if (post.getPitchId() == null || post.getPitchId().isEmpty()) {
            Toast.makeText(getContext(), "Pitch information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading message
        Toast.makeText(getContext(), "Loading location...", Toast.LENGTH_SHORT).show();

        // Get pitch location from Firebase
        DatabaseReference pitchRef = FirebaseDatabase
                .getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("pitches")
                .child(post.getPitchId());

        pitchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child("location").exists()) {
                    try {
                        // Get latitude and longitude
                        String latitude = snapshot.child("location").child("latitude").getValue(String.class);
                        String longitude = snapshot.child("location").child("longitude").getValue(String.class);
                        String pitchName = snapshot.child("name").getValue(String.class);

                        if (latitude != null && longitude != null) {
                            // Create Google Maps intent
                            String uri = String.format(Locale.ENGLISH,
                                    "geo:%s,%s?q=%s,%s(%s)",
                                    latitude, longitude, latitude, longitude,
                                    pitchName != null ? pitchName : "Football Pitch");

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");

                            // Check if Google Maps is installed
                            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                getContext().startActivity(intent);
                            } else {
                                // Fallback to web browser if Google Maps is not installed
                                String webUri = String.format(Locale.ENGLISH,
                                        "https://www.google.com/maps?q=%s,%s(%s)",
                                        latitude, longitude,
                                        pitchName != null ? pitchName : "Football Pitch");
                                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                                getContext().startActivity(webIntent);
                            }
                        } else {
                            Toast.makeText(getContext(), "Location coordinates not available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PostDetailDialog", "Error parsing location data: " + e.getMessage());
                        Toast.makeText(getContext(), "Error loading location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Location not found for this pitch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostDetailDialog", "Firebase error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "open":
                return R.color.status_open;
            case "closed":
                return R.color.status_close;
            case "pending":
                return R.color.status_pending;
            default:
                return R.color.primary_green;
        }
    }

    private String formatDateTime(String dateTimeString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(dateTimeString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeString;
        }
    }

    private String getRelativeTime(String dateTimeString){
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(dateTimeString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeString;
        }
    }
}