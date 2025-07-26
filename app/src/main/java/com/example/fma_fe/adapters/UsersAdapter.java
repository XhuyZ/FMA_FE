// com.example.fma_fe.adapters.UsersAdapter.java
package com.example.fma_fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fma_fe.R;
import com.example.fma_fe.activities.ProfileFragment;
import com.example.fma_fe.models.User;
import java.util.List;

public class UsersAdapter
        extends RecyclerView.Adapter<UsersAdapter.VH> {

    public interface OnUserClick {
        void openChat(String otherUserId);
    }

    private final List<User> data;
    private final OnUserClick callback;

    public UsersAdapter(List<User> data, OnUserClick cb) {
        this.data     = data;
        this.callback = cb;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VH(v);
    }


    // Trong UsersAdapter
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User u = data.get(position);
        holder.tv.setText(u.getName());
        holder.itemView.setOnClickListener(v ->
                callback.openChat(u.getUid())); // Dùng getUid() thay vì getUserId()
    }


    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) {
            super(v);
            tv = v.findViewById(android.R.id.text1);
        }
    }
}
