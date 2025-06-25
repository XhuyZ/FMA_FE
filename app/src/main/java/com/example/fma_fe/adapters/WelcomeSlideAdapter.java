package com.example.fma_fe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fma_fe.R;
import com.example.fma_fe.models.WelcomeSlide;
import java.util.List;

public class WelcomeSlideAdapter extends RecyclerView.Adapter<WelcomeSlideAdapter.SlideViewHolder> {
    
    private List<WelcomeSlide> slides;
    
    public WelcomeSlideAdapter(List<WelcomeSlide> slides) {
        this.slides = slides;
    }
    
    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slide_welcome, parent, false);
        return new SlideViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        WelcomeSlide slide = slides.get(position);
        holder.bind(slide);
    }
    
    @Override
    public int getItemCount() {
        return slides.size();
    }
    
    static class SlideViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleText;
        private TextView descriptionText;
        
        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            titleText = itemView.findViewById(R.id.textTitle);
            descriptionText = itemView.findViewById(R.id.textDescription);
        }
        
        public void bind(WelcomeSlide slide) {
            imageView.setImageResource(slide.getImageResource());
            titleText.setText(slide.getTitle());
            descriptionText.setText(slide.getDescription());
        }
    }
}
