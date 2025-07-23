package com.example.fma_fe.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fma_fe.R;
import com.example.fma_fe.models.Match;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final Context context;
    private final List<Match> matchList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onMatchClick(Match match);
    }

    public MatchAdapter(Context context, List<Match> matchList, OnItemClickListener listener) {
        this.context = context;
        this.matchList = matchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match_card, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.bind(match, listener, context);
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        Chip chipStatus;
        TextView txtMatchDate, txtTeam2Name, txtStartTime, txtDuration, txtPitchName, txtPitchAddress, txtPlayerCount;
        MaterialCardView cardMatch;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            chipStatus = itemView.findViewById(R.id.chip_match_status);
            txtMatchDate = itemView.findViewById(R.id.txt_match_date);
            txtTeam2Name = itemView.findViewById(R.id.txt_team2_name);
            txtStartTime = itemView.findViewById(R.id.txt_start_time);
            txtDuration = itemView.findViewById(R.id.txt_duration);
            txtPitchName = itemView.findViewById(R.id.txt_pitch_name);
            txtPitchAddress = itemView.findViewById(R.id.txt_pitch_address);
            txtPlayerCount = itemView.findViewById(R.id.txt_players_count);
            cardMatch = itemView.findViewById(R.id.card_match);
        }

        public void bind(final Match match, final OnItemClickListener listener, Context context) {
            // 1. Set Status and Color
            String status = match.getStatus();
            chipStatus.setText(status);
            if (status.equalsIgnoreCase("Upcoming")) {
                chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_green)));
            } else if (status.equalsIgnoreCase("Completed")) {
                chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lavender)));
            } else { // "Today" or other statuses
                chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_green)));
            }

            // 2. Set Date and Time from startTime
            Date date = parseIsoDate(match.getStartTime());
            if (date != null) {
                txtMatchDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date));
                txtStartTime.setText(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(date));
            } else {
                txtMatchDate.setText("N/A");
                txtStartTime.setText("N/A");
            }

            // 3. Set Opponent Team Name
            txtTeam2Name.setText(match.getTeam_id()); // Lấy tên đội đối thủ từ model

            // 4. Set Placeholder Data for fields not in the model
            // TODO: Thay thế bằng logic lấy dữ liệu thật khi có API
            txtPitchName.setText("Sân " + match.getPitchId());
            txtPitchAddress.setText("Địa chỉ sẽ được cập nhật sau");
            txtDuration.setText("2h"); // Giả định trận đấu kéo dài 2 tiếng
            txtPlayerCount.setText("22 players"); // Giả định đủ 22 người

            // 5. Set Click Listener
            cardMatch.setOnClickListener(v -> listener.onMatchClick(match));
        }

        private Date parseIsoDate(String isoTime) {
            // Định dạng này khớp với dữ liệu mẫu bạn tạo ra
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            try {
                return inputFormat.parse(isoTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}