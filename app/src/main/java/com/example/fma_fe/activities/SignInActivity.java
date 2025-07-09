package com.example.fma_fe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fma_fe.MainActivity;
import com.example.fma_fe.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextPassword;
    private TextInputLayout textInputLayoutUsername, textInputLayoutPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar progressBar;
    private TextView textForgotPassword, btnToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutUsername = findViewById(R.id.textInputLayoutUsername);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        progressBar = findViewById(R.id.progressBar);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        btnToSignup = findViewById(R.id.btnToSignup);
    }

    private void setClickListeners() {
        // Sign In button click listener
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignIn();
            }
        });

        // Sign Up text click listener
        btnToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sign Up activity (if you have one)
                // Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                // startActivity(intent);
            }
        });

        // Forgot Password text click listener
        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password
                // You can implement this later
            }
        });
    }

    private void handleSignIn() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Reset any previous errors
        textInputLayoutUsername.setError(null);
        textInputLayoutPassword.setError(null);

        // Basic validation
        if (username.isEmpty()) {
            textInputLayoutUsername.setError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            textInputLayoutPassword.setError("Password is required");
            return;
        }

        // Show progress bar and hide button
        showLoading(true);

        // Simulate sign in process (you can replace this with actual authentication)
        // For now, we'll just navigate to MainActivity after a short delay
        buttonSignIn.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide progress bar
                showLoading(false);

                // Navigate to MainActivity
                navigateToMainActivity();
            }
        }, 1000); // 1 second delay to simulate network request
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            buttonSignIn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SignInActivity so user can't go back to it
    }
}