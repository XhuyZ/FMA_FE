package com.example.fma_fe.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fma_fe.R;
import com.example.fma_fe.models.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENT = 1, RECEIVED = 2;

    private final Context ctx;
    private final List<ChatMessage> data;
    private final String myId;

    public ChatMessageAdapter(Context ctx, List<ChatMessage> data, String myId) {
        this.ctx = ctx; this.data = data; this.myId = myId;
    }

    @Override public int getItemViewType(int pos) {
        return data.get(pos).getSenderId().equals(myId) ? SENT : RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        int layout = vt == SENT ? R.layout.item_message_sent
                : R.layout.item_message_received;
        View v = LayoutInflater.from(ctx).inflate(layout, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        ((VH) h).bind(data.get(pos));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView msg, time, name;
        VH(View v) {
            super(v);
            msg  = v.findViewById(R.id.tv_message);
            time = v.findViewById(R.id.tv_time);
            name = v.findViewById(R.id.tv_sender_name); // null ở layout sent
        }
        void bind(ChatMessage m) {
            msg.setText(m.getMessage());
            time.setText(DateFormat.format("HH:mm", m.getTimestamp()));
            if (name != null) name.setText(m.getSenderName());
        }
    }
}

