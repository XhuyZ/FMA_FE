package com.example.fma_fe.activities;

import android.os.Bundle;
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
        // Sample data - replace with actual API call
        allPosts.clear();

        Post post1 = new Post();
        post1.setPostId(1);
        post1.setTeamId(1);
        post1.setPostedByPlayerId(1);
        post1.setPitchId(55);
        post1.setReceivingTeamId(null);
        post1.setMatchTime("2025-07-03T18:00:00Z");
        post1.setDescription("Chúng tôi tìm đối thủ vào tối thứ 7");
        post1.setLookingFor("Opponent");
        post1.setPostStatus("Open");
        post1.setImageUrl("https://i.pinimg.com/736x/d4/a0/39/d4a039be5eea48c290126e548236ef64.jpg");
        post1.setCreatedAt("2025-06-29T10:00:00Z");
        post1.setUpdatedAt("2025-07-01T15:30:00Z");

        Post post2 = new Post();
        post2.setPostId(2);
        post2.setTeamId(102);
        post2.setPostedByPlayerId(1002);
        post2.setPitchId(56);
        post2.setReceivingTeamId(103);
        post2.setMatchTime("2025-07-05T20:00:00Z");
        post2.setDescription("Cần thêm đồng đội cho trận đấu 7 người");
        post2.setLookingFor("Teammate");
        post2.setPostStatus("Open");
        post2.setImageUrl("https://i.pinimg.com/736x/92/ee/dd/92eeddbe026c3c0451a1f74f8a1af63e.jpg");
        post2.setCreatedAt("2025-06-30T09:45:00Z");
        post2.setUpdatedAt("2025-07-01T15:30:00Z");

        allPosts.add(post1);
        allPosts.add(post2);

        filterPosts();
    }
}