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

import com.example.fma_fe.R;
import com.example.fma_fe.adapters.MatchAdapter;
import com.example.fma_fe.models.Match;
import com.example.fma_fe.ExpandedRecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MatchFragment extends Fragment implements MatchAdapter.OnItemClickListener {

  private ExpandedRecyclerView recyclerMatches;
  private MatchAdapter matchAdapter;
  private LinearLayout layoutEmptyState;
  private ImageView btnRefresh, btnCalendar;
  private ChipGroup chipGroupFilter;

  private final List<Match> allMatches = new ArrayList<>();
  private final List<Match> filteredMatches = new ArrayList<>();

  public MatchFragment() {
    // Required empty constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_match, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initViews(view);
    setupRecyclerView();
    setupListeners();
    loadSampleData();
  }

  private void initViews(View view) {
    recyclerMatches = view.findViewById(R.id.recycler_matches);
    layoutEmptyState = view.findViewById(R.id.layout_empty_state);
    btnRefresh = view.findViewById(R.id.btn_refresh);
    btnCalendar = view.findViewById(R.id.btn_calendar);
    chipGroupFilter = view.findViewById(R.id.chip_group_filter);
  }

  private void setupRecyclerView() {
    // Khởi tạo adapter với context, danh sách và listener
    matchAdapter = new MatchAdapter(getContext(), filteredMatches, this);
    recyclerMatches.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerMatches.setAdapter(matchAdapter);
  }

  private void setupListeners() {
    btnRefresh.setOnClickListener(v -> {
      Toast.makeText(getContext(), "Đang tải lại trận đấu...", Toast.LENGTH_SHORT).show();
      loadSampleData();
    });

    btnCalendar.setOnClickListener(v -> {
      Toast.makeText(getContext(), "Chức năng lịch sẽ sớm ra mắt!", Toast.LENGTH_SHORT).show();
    });

    // Sử dụng ChipGroup để quản lý việc lọc, mã nguồn sẽ gọn hơn
    chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> filterMatches());
  }

  private void loadSampleData() {
    DatabaseReference appointmentRef = FirebaseDatabase
            .getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("appointments");

    allMatches.clear();

    appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        long totalAppointments = snapshot.getChildrenCount();
        final long[] loaded = {0};

        for (DataSnapshot appointmentSnap : snapshot.getChildren()) {
          int id = appointmentSnap.child("id").getValue(Integer.class);
          String pitchId = appointmentSnap.child("pitchId").getValue(String.class);
          String startTime = appointmentSnap.child("startTime").getValue(String.class);
          String status = appointmentSnap.child("status").getValue(String.class);
          String teamId = appointmentSnap.child("team_id").getValue(String.class);

          // Tạo Match tạm thời, sẽ set pitchName & teamName sau
          Match match = new Match(id, pitchId, startTime, status, "");

          // Truy vấn pitch
          DatabaseReference pitchRef = FirebaseDatabase.getInstance()
                  .getReference("pitches").child(pitchId);

          pitchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot pitchSnap) {
              String pitchName = pitchSnap.child("name").getValue(String.class);
              match.setPitchId(pitchName != null ? pitchName : "Không rõ sân");

              // Truy vấn team
              DatabaseReference teamRef = FirebaseDatabase.getInstance()
                      .getReference("teams").child(teamId);

              teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot teamSnap) {
                  String teamName = teamSnap.child("name").getValue(String.class);
                  match.setTeam_id(teamName != null ? teamName : "Không rõ đội");

                  allMatches.add(match);
                  loaded[0]++;
                  if (loaded[0] == totalAppointments) {
                    if (chipGroupFilter.getCheckedChipId() == -1) {
                      chipGroupFilter.check(R.id.chip_all_matches);
                    } else {
                      filterMatches();
                    }
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                  Log.e("FirebaseError", "Lỗi team: " + error.getMessage());
                  loaded[0]++;
                  if (loaded[0] == totalAppointments) {
                    filterMatches();
                  }
                }
              });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              Log.e("FirebaseError", "Lỗi pitch: " + error.getMessage());
              loaded[0]++;
              if (loaded[0] == totalAppointments) {
                filterMatches();
              }
            }
          });
        }

        if (totalAppointments == 0) {
          filterMatches();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e("FirebaseError", "Lỗi: " + error.getMessage());
      }
    });
  }

  private void filterMatches() {
    filteredMatches.clear();
    int checkedId = chipGroupFilter.getCheckedChipId();

    if (checkedId == R.id.chip_all_matches) {
      filteredMatches.addAll(allMatches);
    } else {
      String filter = "";
      if (checkedId == R.id.chip_today) filter = "Today";
      else if (checkedId == R.id.chip_upcoming) filter = "Upcoming";
      else if (checkedId == R.id.chip_completed) filter = "Completed";

      for (Match match : allMatches) {
        // Lọc theo status cho "Upcoming" và "Completed"
        if (!filter.equals("Today") && match.getStatus().equalsIgnoreCase(filter)) {
          filteredMatches.add(match);
        }
        // Lọc theo ngày cho "Today"
        else if (filter.equals("Today") && isSameDay(match.getStartTime(), new Date())) {
          filteredMatches.add(match);
        }
      }
    }

    matchAdapter.notifyDataSetChanged();
    updateEmptyState();
  }

  private void updateEmptyState() {
    if (filteredMatches.isEmpty()) {
      layoutEmptyState.setVisibility(View.VISIBLE);
      recyclerMatches.setVisibility(View.GONE);
    } else {
      layoutEmptyState.setVisibility(View.GONE);
      recyclerMatches.setVisibility(View.VISIBLE);
    }
  }


  // Hàm tạo chuỗi ngày tháng ISO 8601 để làm dữ liệu mẫu
  private String getDateOffset(int daysOffset) {
    long now = System.currentTimeMillis();
    long offset = daysOffset * 24L * 60L * 60L * 1000L;
    Date date = new Date(now + offset);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }

  // Hàm kiểm tra cùng ngày
  private boolean isSameDay(String dateString, Date today) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      Date matchDate = sdf.parse(dateString);
      SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
      return fmt.format(matchDate).equals(fmt.format(today));
    } catch (ParseException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void onMatchClick(Match match) {
    Toast.makeText(getContext(), "Clicked on match with " + match.getTeam_id(), Toast.LENGTH_SHORT).show();
    // TODO: Điều hướng đến màn hình chi tiết trận đấu
  }
}