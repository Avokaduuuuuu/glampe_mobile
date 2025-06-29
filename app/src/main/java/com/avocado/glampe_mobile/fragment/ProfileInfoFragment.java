package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;

public class ProfileInfoFragment extends Fragment {

    ImageView backArrow;
    NavController navController;
    private AuthUserResponse user;
    AppCompatEditText etFirstName, etLastName, etEmail, etAddress, etPhone;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_info, container, false);
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
        backArrow = view.findViewById(R.id.btn_back);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
    }

    private void onClickListener() {
        backArrow.setOnClickListener(v -> navController.popBackStack());

    }

    private void updateView(){
        if (user != null) {
            etFirstName.setText(user.getUser().getFirstName());
            etLastName.setText(user.getUser().getLastName());
            etEmail.setText(user.getUser().getEmail());
            etAddress.setText(user.getUser().getAddress());
            etPhone.setText(user.getUser().getPhoneNumber());
        }
    }
}
