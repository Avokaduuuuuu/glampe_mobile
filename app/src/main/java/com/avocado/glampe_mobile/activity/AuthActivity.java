package com.avocado.glampe_mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.avocado.glampe_mobile.R;

public class AuthActivity extends AppCompatActivity {

    LinearLayout guestBrowseButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initiateView();
        onClickListener();
    }

    private void initiateView(){
        guestBrowseButton = findViewById(R.id.guestBrowseButton);
    }

    private void onClickListener(){
        guestBrowseButton.setOnClickListener(v -> navigateWithoutLogin());
    }

    private void navigateWithoutLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
