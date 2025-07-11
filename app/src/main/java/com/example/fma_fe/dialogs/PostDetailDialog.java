package com.example.fma_fe.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import com.google.android.material.button.MaterialButton;
import com.example.fma_fe.R;
import com.example.fma_fe.models.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PostDetailDialog extends Dialog {
    private Post post;
    private OnActionClickListener listener;

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
        TextView txtPitchInfo = findViewById(R.id.txt_pitch_info_detail);
        TextView txtCreatedAt = findViewById(R.id.txt_created_at_detail);
        TextView txtTeamInfo = findViewById(R.id.txt_team_info);
        TextView txtPlayerInfo = findViewById(R.id.txt_player_info);
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

        txtPitchInfo.setText("Pitch #" + post.getPitchId());

//        String relativeTime = getRelativeTime(post.getCreatedAt());
//        txtCreatedAt.setText("Posted " + relativeTime);

        txtTeamInfo.setText("Team ID: " + post.getTeamId());
        txtPlayerInfo.setText("Posted by Player ID: " + post.getPostedByPlayerId());

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

        // Enable button based on post status
        btnContact.setEnabled(post.getPostStatus().equals("Open"));
        if (!post.getPostStatus().equals("Open")) {
            btnContact.setText("Post is " + post.getPostStatus());
        }
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