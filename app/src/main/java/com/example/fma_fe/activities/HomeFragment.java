package com.example.fma_fe.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.fma_fe.R;
import com.example.fma_fe.adapters.PostAdapter;
import com.example.fma_fe.models.Post;
import com.example.fma_fe.dialogs.PostDetailDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private RecyclerView recyclerPosts;
    private PostAdapter postsAdapter;
    private LinearLayout layoutEmptyState;
    private ImageView btnRefresh;
    private Chip chipAll, chipOpponent, chipTeammate, chipOpen;

    private List<Post> allPosts = new ArrayList<>();
    private List<Post> filteredPosts = new ArrayList<>();
    private String currentFilter = "All";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onPostClick(Post post) {
        // Xử lý khi user click vào 1 post ở RecyclerView
        // Ví dụ mở PostDetailDialog hoặc chuyển sang PostDetailActivity
        Toast.makeText(getContext(), "Clicked post ID: " + post.getPostId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExpandClick(Post post) {
        // Xử lý khi user click vào nút expand
        // Ví dụ: mở dialog với thông tin chi tiết hoặc expand/collapse nội dung
        Toast.makeText(getContext(), "Expand clicked for post ID: " + post.getPostId(), Toast.LENGTH_SHORT).show();

        // Bạn có thể implement logic expand ở đây, ví dụ:
        // - Mở PostDetailDialog
        // - Expand/collapse description
        // - Hiển thị thêm thông tin chi tiết
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupFilterChips();
        setupRefreshButton();
        loadSampleData(); // Replace with actual API call
    }

    private void initViews(View view) {
        recyclerPosts = view.findViewById(R.id.recycler_posts);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        chipAll = view.findViewById(R.id.chip_all);
        chipOpponent = view.findViewById(R.id.chip_opponent);
        chipTeammate = view.findViewById(R.id.chip_teammate);
        chipOpen = view.findViewById(R.id.chip_open);
    }

    private void setupRecyclerView() {
        postsAdapter = new PostAdapter(getContext(), filteredPosts);
        postsAdapter.setOnPostClickListener(this);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPosts.setAdapter(postsAdapter);
    }

    private void setupFilterChips() {
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "All";
                filterPosts();
                uncheckOtherChips(chipAll);
            }
        });

        chipOpponent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "Opponent";
                filterPosts();
                uncheckOtherChips(chipOpponent);
            }
        });

        chipTeammate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "Teammate";
                filterPosts();
                uncheckOtherChips(chipTeammate);
            }
        });

        chipOpen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "Open";
                filterPosts();
                uncheckOtherChips(chipOpen);
            }
        });
    }

    private void uncheckOtherChips(Chip selectedChip) {
        if (selectedChip != chipAll) chipAll.setChecked(false);
        if (selectedChip != chipOpponent) chipOpponent.setChecked(false);
        if (selectedChip != chipTeammate) chipTeammate.setChecked(false);
        if (selectedChip != chipOpen) chipOpen.setChecked(false);
    }

    private void setupRefreshButton() {
        btnRefresh.setOnClickListener(v -> {
            refreshPosts();
        });
    }

    private void filterPosts() {
        filteredPosts.clear();

        for (Post post : allPosts) {
            boolean shouldInclude = false;

            switch (currentFilter) {
                case "All":
                    shouldInclude = true;
                    break;
                case "Opponent":
                    shouldInclude = post.getLookingFor().equals("Opponent");
                    break;
                case "Teammate":
                    shouldInclude = post.getLookingFor().equals("Teammate");
                    break;
                case "Open":
                    shouldInclude = post.getPostStatus().equals("Open");
                    break;
            }

            if (shouldInclude) {
                filteredPosts.add(post);
            }
        }

        postsAdapter.updatePosts(filteredPosts);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredPosts.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerPosts.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerPosts.setVisibility(View.VISIBLE);
        }
    }

    private void refreshPosts() {
        // TODO: Implement actual API call
        Toast.makeText(getContext(), "Refreshing posts...", Toast.LENGTH_SHORT).show();
        loadSampleData();
    }

    private void loadSampleData() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("matchposts");


        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allPosts.clear();
                Log.d("FirebaseDebug", "Tổng số post: " + snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        Log.d("FirebaseDebug", "Tải post thành công: " + post.getDescription());
                        allPosts.add(post);
                    } else {
                        Log.w("FirebaseDebug", "Không map được post: " + postSnapshot.getKey());
                    }
                }

                filterPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi: " + error.getMessage());
            }
        });
    }


}