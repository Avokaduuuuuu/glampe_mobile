package com.avocado.glampe_mobile.activity;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.viewModel.BottomNavViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private BottomNavViewModel bottomNavViewModel;
    private boolean isAnimating = false;

    Set<Integer> destinationsWithoutBottomNav = new HashSet<>(Arrays.asList(
            R.id.campSiteDetailFragment,
            R.id.profileInfoFragment,
            R.id.bookingDetailFragment,
            R.id.paymentFailFragment,
            R.id.paymentSuccessFragment
    ));


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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


}
