package com.example.fma_fe.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fma_fe.R;
import com.example.fma_fe.adapters.ChatMessageAdapter;
import com.example.fma_fe.models.ChatMessage;
import com.example.fma_fe.models.ChatRoom;
import com.example.fma_fe.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomActivity extends AppCompatActivity {

    private static final String TAG = "RoomActivity";

    private Toolbar toolbar;
    private RecyclerView rv;
    private TextInputEditText et;
    private FloatingActionButton btnSend;

    private final List<ChatMessage> list = new ArrayList<>();
    private ChatMessageAdapter adapter;

    private String currentUid;
    private String partnerUid;
    private String roomId;

    private DatabaseReference messagesRef, chatRoomsRef, usersRef;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_room);

        roomId     = getIntent().getStringExtra("roomId");
        partnerUid = getIntent().getStringExtra("partnerId"); // (tuỳ chọn) truyền từ ChatActivity
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbarRoom);
        rv      = findViewById(R.id.rvMessages);
        et      = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ChatMessageAdapter(this, list, currentUid);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        messagesRef  = db.getReference("messages").child(roomId);
        chatRoomsRef = db.getReference("chatRooms").child(roomId);
        usersRef     = db.getReference("users");

        loadPartnerName();
        listenMessages();
        handleSend();
    }

    private void loadPartnerName() {
        // Nếu partnerUid chưa có, suy ra từ roomId
        if (partnerUid == null) {
            String[] ids = roomId.split("_");
            partnerUid = ids[0].equals(currentUid) ? ids[1] : ids[0];
        }
        usersRef.child(partnerUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        User u = ds.getValue(User.class);
                        if (u != null) toolbar.setTitle(u.getName());
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) { }
                });
    }

    private void handleSend() {
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable s){
                btnSend.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        btnSend.setOnClickListener(v -> {
            String txt = et.getText().toString().trim();
            if (txt.isEmpty()) return;

            String msgId = messagesRef.push().getKey();
            ChatMessage msg = new ChatMessage(
                    msgId, currentUid, "Bạn",
                    txt, System.currentTimeMillis(), "text"
            );
            messagesRef.child(msgId).setValue(msg);
            et.setText("");

            // update lastMessage
            Map<String,Object> upd = new HashMap<>();
            upd.put("lastMessage", txt);
            upd.put("lastMessageTime", System.currentTimeMillis());
            chatRoomsRef.updateChildren(upd);
        });
    }

    private void listenMessages() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(@NonNull DataSnapshot ds,@androidx.annotation.Nullable String p) {
                ChatMessage m = ds.getValue(ChatMessage.class);
                if (m != null) {
                    list.add(m);
                    adapter.notifyItemInserted(list.size()-1);
                    rv.scrollToPosition(list.size()-1);
                }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot ds,@androidx.annotation.Nullable String p){}
            @Override public void onChildRemoved(@NonNull DataSnapshot ds){}
            @Override public void onChildMoved(@NonNull DataSnapshot ds,@androidx.annotation.Nullable String p){}
            @Override public void onCancelled(@NonNull DatabaseError e){
                Log.e(TAG,"listen cancelled",e.toException());
            }
        });
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
