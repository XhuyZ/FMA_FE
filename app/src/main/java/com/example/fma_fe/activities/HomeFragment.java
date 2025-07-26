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

import com.example.fma_fe.models.Match;
import com.google.android.material.chip.Chip;
import com.example.fma_fe.R;
import com.example.fma_fe.adapters.PostAdapter;
import com.example.fma_fe.models.Post;
import com.example.fma_fe.dialogs.PostDetailDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.fma_fe.MainActivity;
import com.example.fma_fe.models.Appointment;

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
        PostDetailDialog dialog = new PostDetailDialog(requireContext(), post);
        dialog.setOnActionClickListener(new PostDetailDialog.OnActionClickListener() {
            @Override
            public void onContactClick(Post post) {
                createAppointmentForPost(post);
            }

            @Override
            public void onCloseClick() {
                Toast.makeText(getContext(), "Dialog closed", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onExpandClick(Post post) {
        Toast.makeText(getContext(), "Expand clicked for post ID: " + post.getPostId(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getContext(), "Refreshing posts...", Toast.LENGTH_SHORT).show();
        loadSampleData();
    }

    private void loadSampleData() {
        DatabaseReference postsRef = FirebaseDatabase
                .getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("matchposts");

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allPosts.clear();
                Log.d("FirebaseDebug", "Tổng số post: " + snapshot.getChildrenCount());

                long totalPosts = snapshot.getChildrenCount();
                final long[] loadedPosts = {0};

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        String pitchKey = post.getPitchId();
                        Log.d("PitchDebug", "Đang lấy pitch với key: " + pitchKey);

                        DatabaseReference pitchRef = FirebaseDatabase
                                .getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("pitches")
                                .child(pitchKey);

                        pitchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot pitchSnapshot) {
                                if (pitchSnapshot.exists()) {
                                    String pitchName = pitchSnapshot.child("name").getValue(String.class);
                                    post.setPitchName(pitchName != null ? pitchName : "Không rõ sân");
                                } else {
                                    post.setPitchName("Không rõ sân");
                                }

                                allPosts.add(post);
                                Log.d("FirebaseDebug", "Post: " + post.getDescription() + ", Sân: " + post.getPitchName());

                                loadedPosts[0]++;
                                if (loadedPosts[0] == totalPosts) {
                                    filterPosts();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", "Lỗi khi tải pitch: " + error.getMessage());
                                loadedPosts[0]++;
                                if (loadedPosts[0] == totalPosts) {
                                    filterPosts();
                                }
                            }
                        });

                    } else {
                        Log.w("FirebaseDebug", "Không map được post: " + postSnapshot.getKey());
                        loadedPosts[0]++;
                        if (loadedPosts[0] == totalPosts) {
                            filterPosts();
                        }
                    }
                }

                if (totalPosts == 0) {
                    filterPosts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi: " + error.getMessage());
            }
        });
    }

    private void createAppointmentForPost(Post post) {
        String dbUrl = "https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app";

        DatabaseReference usersRef = FirebaseDatabase
                .getInstance(dbUrl)
                .getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String foundTeamId = null;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    try {
                        // Đọc userId dạng Integer
                        Integer userId = userSnapshot.child("userId").getValue(Integer.class);

                        // Đọc teamId có thể là Integer hoặc String "team_2"
                        Object rawTeamId = userSnapshot.child("teamId").getValue();
                        Integer teamId = null;

                        if (rawTeamId instanceof Long) {
                            teamId = ((Long) rawTeamId).intValue(); // nếu là số
                        } else if (rawTeamId instanceof String) {
                            String teamIdStr = (String) rawTeamId;
                            if (teamIdStr.startsWith("team_")) {
                                teamId = Integer.parseInt(teamIdStr.replace("team_", ""));
                            } else {
                                teamId = Integer.parseInt(teamIdStr);
                            }
                        }

                        // Nếu userId trùng với người đăng bài
                        if (userId != null && teamId != null && userId.equals(post.getPostedByPlayerId())) {
                            foundTeamId = "team_" + teamId;
                            break;
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Lỗi parse userId/teamId: " + e.getMessage());
                    }
                }

                if (foundTeamId == null) {
                    Toast.makeText(getContext(), "Không tìm thấy team của user đăng bài", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi tiếp tạo appointment như cũ...
                DatabaseReference appointmentsRef = FirebaseDatabase
                        .getInstance(dbUrl)
                        .getReference("appointments");

                String newKey = "appointment_" + post.getPostId();
                Match newMatch = new Match();

                newMatch.setId(post.getPostId());
                newMatch.setPitchId(post.getPitchId());
                newMatch.setPostId("post_" + post.getPostId());
                newMatch.setStartTime(post.getMatchTime());
                newMatch.setStatus("Pending");
                newMatch.setTeam_id(foundTeamId);

                appointmentsRef.child(newKey).setValue(newMatch)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Match (Appointment) created!", Toast.LENGTH_SHORT).show();
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).replaceFragment(new ContactFragment());
                                }
                            }, 300);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi tạo appointment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi đọc user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }




}