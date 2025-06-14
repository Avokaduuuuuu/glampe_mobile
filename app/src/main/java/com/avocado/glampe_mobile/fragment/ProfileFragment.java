package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.avocado.glampe_mobile.R;

public class ProfileFragment extends Fragment {
    LinearLayout profileInfo, bookingHistory, security, logout;
    NavController navController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        profileInfo = view.findViewById(R.id.layoutPersonalInfo);
        bookingHistory = view.findViewById(R.id.layoutBookingHistory);
        security = view.findViewById(R.id.layoutSecurity);
        logout = view.findViewById(R.id.layoutLogout);

        profileInfo.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_profileInfoFragment);
        });
    }
}
