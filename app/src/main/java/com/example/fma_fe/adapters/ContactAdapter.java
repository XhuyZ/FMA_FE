package com.example.fma_fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fma_fe.R;
import com.example.fma_fe.models.Match;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.PendingVH> {

    public interface OnAcceptClickListener {
        void onAccept(Match match, int position);
    }

    private final List<Match> pendingList;
    private final OnAcceptClickListener listener;

    public ContactAdapter(List<Match> pendingList, OnAcceptClickListener listener) {
        this.pendingList = pendingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PendingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending, parent, false);
        return new PendingVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingVH holder, int position) {
        Match m = pendingList.get(position);
        holder.tvPitchTeam.setText(m.getPitchId() + " — " + m.getTeam_id());
        holder.tvStartTime.setText(formatTime(m.getStartTime()));
        holder.btnAccept.setOnClickListener(v ->
                listener.onAccept(m, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return pendingList.size(); }

    static class PendingVH extends RecyclerView.ViewHolder {
        TextView tvPitchTeam, tvStartTime;
        Button btnAccept;
        PendingVH(View itemView) {
            super(itemView);
            tvPitchTeam = itemView.findViewById(R.id.tv_pitch_team);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }

    private String formatTime(String iso) {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = inFmt.parse(iso);
            SimpleDateFormat out = new SimpleDateFormat(
                    "dd/MM HH:mm", Locale.getDefault());
            return out.format(d);
        } catch (ParseException | NullPointerException e) {
            return iso;
        }
    }
}
