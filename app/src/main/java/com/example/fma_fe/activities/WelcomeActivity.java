package com.example.fma_fe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.fma_fe.R;
import com.example.fma_fe.adapters.WelcomeSlideAdapter;
import com.example.fma_fe.models.WelcomeSlide;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    
    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button btnNext, btnGetStarted;
    private TextView btnSkip;
    private WelcomeSlideAdapter adapter;
    private List<WelcomeSlide> slides;
    private int currentSlide = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_welcome);
            
            initViews();
            setupSlides();
            setupViewPager();
            setupClickListeners();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to SignIn if there's an error
            navigateToSignIn();
        }
    }
    
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        btnGetStarted = findViewById(R.id.btnGetStarted);
    }
    
    private void setupSlides() {
        slides = new ArrayList<>();
        

        slides.add(new WelcomeSlide(
            R.drawable.slide1,
            "Connect with Teams",
            ""
        ));
        
        slides.add(new WelcomeSlide(
                R.drawable.slide2,
            "Schedule Matches",
                ""
        ));
        
        slides.add(new WelcomeSlide(
                R.drawable.slide3,
                "Book Football Pitches",
                ""
        ));
        
        slides.add(new WelcomeSlide(
                R.drawable.slide4,
                "Post Match Requests",
                ""
        ));
        
        slides.add(new WelcomeSlide(
                R.drawable.slide5,
                "Join the Community",
                ""
        ));
    }
    
    private void setupViewPager() {
        adapter = new WelcomeSlideAdapter(slides);
        viewPager.setAdapter(adapter);
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentSlide = position;
                updateUI();
            }
        });
        
        setupDots();
    }
    
    private void setupClickListeners() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSlide < slides.size() - 1) {
                    viewPager.setCurrentItem(currentSlide + 1, true);
                }
            }
        });
        
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });
        
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });
    }
    
    private void setupDots() {
        dotsLayout.removeAllViews();
        
        for (int i = 0; i < slides.size(); i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            
            if (i == currentSlide) {
                dot.setImageResource(R.drawable.dot_active);
            } else {
                dot.setImageResource(R.drawable.dot_inactive);
            }
            
            dotsLayout.addView(dot);
        }
    }
    
    private void updateUI() {
        setupDots();
        
        if (currentSlide == slides.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnGetStarted.setVisibility(View.VISIBLE);
            btnSkip.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnGetStarted.setVisibility(View.GONE);
            btnSkip.setVisibility(View.VISIBLE);
        }
    }
    
    private void navigateToSignIn() {
        try {
            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
