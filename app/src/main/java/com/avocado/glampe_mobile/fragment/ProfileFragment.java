package com.avocado.glampe_mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.activity.AuthActivity;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;

public class ProfileFragment extends Fragment {
    LinearLayout profileInfo, bookingHistory, security, logout;
    NavController navController;

    TextView tvFirstName, tvEmail;


    private AuthUserResponse user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        updateView();
        onClickListener();

    }

    private void initView(View view){
        user = AuthManager.getAuthResponse(requireContext());
        navController = Navigation.findNavController(view);

        profileInfo = view.findViewById(R.id.layoutPersonalInfo);
        bookingHistory = view.findViewById(R.id.layoutBookingHistory);
        security = view.findViewById(R.id.layoutSecurity);
        logout = view.findViewById(R.id.layoutLogout);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvFirstName = view.findViewById(R.id.tvFirstName);
    }

    private void updateView(){
        if (user != null) {
            tvEmail.setText(user.getUser().getEmail());
            tvFirstName.setText(user.getUser().getFirstName());
        }
    }

    private void onClickListener(){
        profileInfo.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_profileInfoFragment);
        });
        logout.setOnClickListener(v -> {
            AuthManager.clearAuthResponse(requireContext());
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            startActivity(intent);
        });
    }
}
