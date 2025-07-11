package com.example.fma_fe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.example.fma_fe.R;
import com.example.fma_fe.models.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onExpandClick(Post post);
    }

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardPost;
        private Chip chipLookingFor;
        private Chip chipStatus;
        private ImageView imgPost;
        private TextView txtDescription;
        private TextView txtMatchTime;
        private TextView txtPitchInfo;
        private TextView txtCreatedAt;
        private ImageView btnExpand;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPost = itemView.findViewById(R.id.card_post);
            chipLookingFor = itemView.findViewById(R.id.chip_looking_for);
            chipStatus = itemView.findViewById(R.id.chip_status);
            imgPost = itemView.findViewById(R.id.img_post);
            txtDescription = itemView.findViewById(R.id.txt_description);
            txtMatchTime = itemView.findViewById(R.id.txt_match_time);
            txtPitchInfo = itemView.findViewById(R.id.txt_pitch_info);
            txtCreatedAt = itemView.findViewById(R.id.txt_created_at);
            btnExpand = itemView.findViewById(R.id.btn_expand);
        }

        public void bind(Post post) {
            // Set looking for chip
            String lookingForText = post.getLookingFor().equals("Opponent") ?
                    "Looking for Opponent" : "Looking for Teammate";
            chipLookingFor.setText(lookingForText);

            // Set status chip with color
            chipStatus.setText(post.getPostStatus());
            int statusColor = getStatusColor(post.getPostStatus());
            chipStatus.setChipBackgroundColorResource(statusColor);

            // Set description
            txtDescription.setText(post.getDescription());

            // Format and set match time
            String formattedTime = formatDateTime(post.getMatchTime());
            txtMatchTime.setText(formattedTime);

            // Set pitch info
            txtPitchInfo.setText("Pitch #" + post.getPitchId());

            // Set created at with relative time
            String relativeTime = getRelativeTime(post.getCreatedAt());
            txtCreatedAt.setText("Posted " + relativeTime);

            // Load image with Glide
            Glide.with(context)
                    .load(post.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder_soccer)
                            .error(R.drawable.placeholder_soccer)
                            .centerCrop())
                    .into(imgPost);

            // Set click listeners
            cardPost.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostClick(post);
                }
            });

            btnExpand.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExpandClick(post);
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

                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return dateTimeString;
            }
        }

        private String getRelativeTime(String dateTimeString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(dateTimeString);

                long timeDiff = System.currentTimeMillis() - date.getTime();
                long seconds = timeDiff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (days > 0) {
                    return days + (days == 1 ? " day ago" : " days ago");
                } else if (hours > 0) {
                    return hours + (hours == 1 ? " hour ago" : " hours ago");
                } else if (minutes > 0) {
                    return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
                } else {
                    return "Just now";
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return "Recently";
            }
        }
    }
}