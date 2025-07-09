package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.google.android.material.button.MaterialButton;

public class PaymentSuccessFragment extends Fragment {

    TextView tvBookingId, tvCampSiteName, tvDates, tvTotal;
    MaterialButton btnViewBooking, btnBackHome;

    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_success, container,  false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        updateView();
        onClickListener();
    }

    private void initView(View view) {
        tvBookingId = view.findViewById(R.id.tvBookingId);
        tvCampSiteName = view.findViewById(R.id.tvCampSiteName);
        tvDates = view.findViewById(R.id.tvBookingDates);
        tvTotal = view.findViewById(R.id.tvTotalAmount);
        btnViewBooking = view.findViewById(R.id.btnViewBooking);
        btnBackHome = view.findViewById(R.id.btnBackToHome);
        navController = Navigation.findNavController(view);
    }

    private void updateView() {
        if (getArguments() != null) {
            tvBookingId.setText(getArguments().getString("bookingId"));
            tvCampSiteName.setText(getArguments().getString("campSiteName"));
            tvDates.setText(getArguments().getString("bookingDates"));
            tvTotal.setText(PriceFormat.formatUsd(getArguments().getDouble("totalAmount")));
        }
    }

    private void onClickListener(){
        btnViewBooking.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("bookingId", Long.parseLong(tvBookingId.getText().toString()));
            navController.navigate(R.id.bookingDetailFragment, args,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.app_graph, true) // clear the back stack
                            .build()
            );
        });
        btnBackHome.setOnClickListener(v -> navController.navigate(
                R.id.campSiteFragment,
                null,
                new NavOptions.Builder()
                        .setPopUpTo(R.id.app_graph, true)
                        .build()
        ));
    }
}
