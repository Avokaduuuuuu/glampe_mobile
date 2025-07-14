package com.avocado.glampe_mobile.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;

public class SplashActivity extends AppCompatActivity {

    AuthUserResponse user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        user = AuthManager.getAuthResponse(this);
        if (user != null) {
            navigateToMain();
        } else {
            navigateToAuth();
        }

    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAuth(){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
