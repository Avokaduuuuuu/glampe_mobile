package com.avocado.glampe_mobile.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.avocado.glampe_mobile.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        navigateToMain();
    }

    private void navigateToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
