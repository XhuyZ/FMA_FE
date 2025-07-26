package com.example.fma_fe.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.fma_fe.R;
import com.example.fma_fe.adapters.ContactAdapter;
import com.example.fma_fe.adapters.ContactAdapter.OnAcceptClickListener;
import com.example.fma_fe.ExpandedRecyclerView;
import com.example.fma_fe.models.Match;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContactFragment extends Fragment implements OnAcceptClickListener {

  private ExpandedRecyclerView recyclerPending;
  private LinearLayout emptyState;
  private ContactAdapter adapter;
  private final List<Match> pendingList = new ArrayList<>();
  private String currentUserId;

  @Nullable
  @Override
  public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                        @Nullable android.view.ViewGroup container,
                                        @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_contact, container, false);
  }

  @Override
  public void onViewCreated(@NonNull android.view.View view,
                            @Nullable Bundle savedInstanceState) {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
      currentUserId = currentUser.getUid();
    } else {
      Toast.makeText(getContext(), "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
      return;
    }

    super.onViewCreated(view, savedInstanceState);
    recyclerPending = view.findViewById(R.id.recycler_pending);
    emptyState = view.findViewById(R.id.layout_empty_state);

    adapter = new ContactAdapter(pendingList, this);
    recyclerPending.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerPending.setAdapter(adapter);



    loadPending();
  }

  private void loadPending() {
    DatabaseReference ref = FirebaseDatabase
            .getInstance("https://fma-be-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("appointments");

    Query query = ref.orderByChild("status").equalTo("Pending");

    pendingList.clear();
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        final int[] count = {0};
        final int total = (int) snapshot.getChildrenCount();

        // Cần list tạm để chỉ notifyDataSetChanged sau khi duyệt xong hết
        List<Match> tempList = new ArrayList<>();

        for (DataSnapshot snap : snapshot.getChildren()) {
          Match m = snap.getValue(Match.class);
          if (m == null) {
            count[0]++;
            if (count[0] == total) {
              pendingList.clear();
              pendingList.addAll(tempList);
              adapter.notifyDataSetChanged();
              toggleEmpty();
            }
            continue;
          }
          m.setDbKey(snap.getKey());

          // Lấy team_id để check thành viên
          String teamId = m.getTeam_id();
          if (teamId == null || teamId.isEmpty()) {
            count[0]++;
            if (count[0] == total) {
              pendingList.clear();
              pendingList.addAll(tempList);
              adapter.notifyDataSetChanged();
              toggleEmpty();
            }
            continue;
          }

          DatabaseReference teamRef = FirebaseDatabase.getInstance()
                  .getReference("teams").child(teamId).child("members");
          teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot teamSnap) {
              boolean found = false;
              for (DataSnapshot memberSnap : teamSnap.getChildren()) {
                String uid = memberSnap.getValue(String.class);
                if (uid != null && uid.equals(currentUserId)) {
                  found = true;
                  break;
                }
              }
              if (found) {
                tempList.add(m);
              }
              count[0]++;
              // Khi duyệt xong hết thì mới cập nhật UI 1 lần
              if (count[0] == total) {
                pendingList.clear();
                pendingList.addAll(tempList);
                adapter.notifyDataSetChanged();
                toggleEmpty();
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              // vẫn tăng đếm
              count[0]++;
              if (count[0] == total) {
                pendingList.clear();
                pendingList.addAll(tempList);
                adapter.notifyDataSetChanged();
                toggleEmpty();
              }
            }
          });
        }
        // Nếu không có appointment nào
        if (total == 0) {
          pendingList.clear();
          adapter.notifyDataSetChanged();
          toggleEmpty();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }


  private void toggleEmpty() {
    boolean isEmpty = pendingList.isEmpty();
    emptyState.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
    recyclerPending.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
  }

  // ====== Accept click ======
  @Override
  public void onAccept(Match match, int position) {
    DatabaseReference appointmentsRef = FirebaseDatabase.getInstance()
            .getReference("appointments")
            .child(match.getDbKey());
    // 1. Update appointment status
    appointmentsRef.child("status").setValue("Confirmed")
            .addOnSuccessListener(v -> {
              // 2. Nếu có postId --> update post status luôn
              String postId = match.getPostId();
              if (postId != null && !postId.isEmpty()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance()
                        .getReference("matchposts")
                        .child(postId)
                        .child("postStatus");
                postRef.setValue("Close")
                        .addOnSuccessListener(v2 -> {
                          Toast.makeText(getContext(), "Accepted & closed post!", Toast.LENGTH_SHORT).show();
                          // remove UI
                          pendingList.remove(position);
                          adapter.notifyItemRemoved(position);
                          toggleEmpty();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Cập nhật post lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
              } else {
                Toast.makeText(getContext(), "Accepted!", Toast.LENGTH_SHORT).show();
                pendingList.remove(position);
                adapter.notifyItemRemoved(position);
                toggleEmpty();
              }
            })
            .addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Lỗi accept: " + e.getMessage(), Toast.LENGTH_SHORT).show());
  }


}
