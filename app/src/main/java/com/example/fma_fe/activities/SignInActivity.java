package com.example.fma_fe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fma_fe.R;

public class SignInActivity extends AppCompatActivity {

    private Button btnToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnToSignUp = findViewById(R.id.btnToSignUp);
        btnToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }
}

