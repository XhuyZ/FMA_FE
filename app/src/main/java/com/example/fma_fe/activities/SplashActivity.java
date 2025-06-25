// DEBUGGING STEPS - Replace your SplashActivity temporarily with this:

package com.example.fma_fe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fma_fe.R;

public class SplashActivity extends AppCompatActivity {
    
    private static final String TAG = "SplashActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        
        Log.d(TAG, "SplashActivity onCreate started");
        
        new Handler().postDelayed(() -> {
            try {
                Log.d(TAG, "Attempting to start WelcomeActivity");
                
                // Check if WelcomeActivity class exists
                Class<?> welcomeClass = Class.forName("com.example.fma_fe.activities.WelcomeActivity");
                Log.d(TAG, "WelcomeActivity class found: " + welcomeClass.getName());
                
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "WelcomeActivity class not found", e);
                // Fallback to SignInActivity
                try {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    Log.e(TAG, "SignInActivity also failed", ex);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error starting WelcomeActivity", e);
                // Fallback to SignInActivity
                try {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    Log.e(TAG, "SignInActivity also failed", ex);
                }
            }
        }, 3000); // Reduced to 3 seconds for faster testing
    }
}
