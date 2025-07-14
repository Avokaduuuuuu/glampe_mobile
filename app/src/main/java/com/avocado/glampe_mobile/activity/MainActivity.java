package com.avocado.glampe_mobile.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.fcmtoken.req.FcmTokenUpdateRequest;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.viewModel.BottomNavViewModel;
import com.avocado.glampe_mobile.viewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private BottomNavViewModel bottomNavViewModel;
    private boolean isAnimating = false;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;


    Set<Integer> destinationsWithoutBottomNav = new HashSet<>(Arrays.asList(
            R.id.campSiteDetailFragment,
            R.id.profileInfoFragment,
            R.id.bookingDetailFragment,
            R.id.paymentFailFragment,
            R.id.paymentSuccessFragment,
            R.id.chatFragment,
            R.id.cartFragment
    ));


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermission();


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            bottomNavViewModel = new ViewModelProvider(this).get(BottomNavViewModel.class);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destinationsWithoutBottomNav.contains(destination.getId())){
                    bottomNavigationView.setVisibility(View.GONE);
                } else bottomNavigationView.setVisibility(View.VISIBLE);
            });

            observeBottomNavVisibility();
        } else throw new IllegalStateException("NavHostFragment not found");
    }

    private void observeBottomNavVisibility(){
        bottomNavViewModel.getBottomNavVisible().observe(this, isVisible -> {
            Log.d(TAG, "BottomNav visibility changed: " + isVisible);
            if (isVisible != null) {
                if (isVisible) showBottomNavigation();
                else hideBottomNavigation();
            }
        });
    }

    private void hideBottomNavigation() {
        if (isAnimating || bottomNavigationView.getTranslationY() > 0) {
            return; // Đã ẩn rồi hoặc đang animate
        }

        isAnimating = true;
        bottomNavigationView.animate()
                .translationY(bottomNavigationView.getHeight() + 60)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                    }
                })
                .start();
    }

    private void showBottomNavigation() {
        if (isAnimating || bottomNavigationView.getTranslationY() == 0) {
            return; // Đã hiện rồi hoặc đang animate
        }

        isAnimating = true;
        bottomNavigationView.animate()
                .translationY(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                    }
                })
                .start();
    }






    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted");
            } else {
                Log.d("Permission", "Notification permission denied");
                // Handle the case where user denied permission
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification Permission Required")
                .setMessage("Please enable notifications in app settings to receive push notifications.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
