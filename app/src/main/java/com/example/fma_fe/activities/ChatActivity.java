// com.example.fma_fe.activities.ChatActivity.java
package com.example.fma_fe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fma_fe.MainActivity;
import com.example.fma_fe.R;
import com.example.fma_fe.adapters.UsersAdapter;
import com.example.fma_fe.models.ChatRoom;
import com.example.fma_fe.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerUsers;

    private FirebaseAuth mAuth;
    private String currentUserId;

    private DatabaseReference usersRef;
    private DatabaseReference chatRoomsRef;
    private DatabaseReference userChatsRef;

    private final List<User> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_chat);

        toolbar       = findViewById(R.id.toolbar);
        recyclerUsers = findViewById(R.id.recyclerUsers);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser cur = mAuth.getCurrentUser();
        if (cur == null) {
            finish();
            return;
        }
        currentUserId = cur.getUid();

        usersRef      = FirebaseDatabase.getInstance().getReference("users");
        chatRoomsRef  = FirebaseDatabase.getInstance().getReference("chatRooms");
        userChatsRef  = FirebaseDatabase.getInstance().getReference("userChats");

        setupToolbar();
        setupRecycler();
        loadUsers();
    }

    private void setupToolbar() {
        toolbar.setTitle("Chọn người chat");
        toolbar.setNavigationIcon(R.drawable.ic_home);
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    private void setupRecycler() {
        UsersAdapter adapter = new UsersAdapter(usersList, otherId -> {
            openChatWith(otherId);
        });
        recyclerUsers.setLayoutManager(
                new LinearLayoutManager(this));
        recyclerUsers.setAdapter(adapter);
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        usersList.clear();
                        for (DataSnapshot ds : snap.getChildren()) {
                            User u = ds.getValue(User.class);
                            if (u != null) {
                                u.setUid(ds.getKey()); // Gán uid là key của node
                                if (u.getUid() != null && !u.getUid().equals(currentUserId)) {
                                    usersList.add(u);
                                }
                            }

                        }

                        recyclerUsers.getAdapter().notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {
                        Log.e(TAG, "Load users failed", e.toException());
                    }
                });
    }

    private void openChatWith(String otherId) {

        String roomId = currentUserId.compareTo(otherId) < 0
                ? currentUserId + "_" + otherId
                : otherId + "_" + currentUserId;

        chatRoomsRef.child(roomId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        if (!snap.exists()) {
                            Map<String, Boolean> parts = new HashMap<>();
                            parts.put(currentUserId, true);
                            parts.put(otherId,      true);

                            ChatRoom room = new ChatRoom(
                                    roomId, parts, "",
                                    System.currentTimeMillis(), "peer");

                            chatRoomsRef.child(roomId).setValue(room);
                            userChatsRef.child(currentUserId).child(roomId).setValue(true);
                            userChatsRef.child(otherId)     .child(roomId).setValue(true);
                        }

                        Intent i = new Intent(ChatActivity.this, RoomActivity.class);
                        i.putExtra("roomId",   roomId);
                        i.putExtra("partnerId", otherId);
                        startActivity(i);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {
                        Log.e(TAG, "Open chat failed", e.toException());
                    }
                });

    }




}
