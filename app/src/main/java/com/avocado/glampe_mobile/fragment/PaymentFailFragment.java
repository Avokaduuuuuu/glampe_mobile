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

public class PaymentFailFragment extends Fragment {

    TextView tvBookingId, tvCampSiteName, tvDates, tvTotal, tvErrorCode, tvErrorMessage, tvTransactionId;
    MaterialButton btnTryAgain, btnBackHome;

    NavController navController;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_fail, container, false);
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
        btnTryAgain = view.findViewById(R.id.btnTryAgain);
        btnBackHome = view.findViewById(R.id.btnBackToHome);
        navController = Navigation.findNavController(view);
        tvErrorCode = view.findViewById(R.id.tvErrorCode);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        tvTransactionId = view.findViewById(R.id.tvTransactionId);
    }

    private void updateView() {
        if (getArguments() != null) {
            tvBookingId.setText(getArguments().getString("bookingId"));
            tvCampSiteName.setText(getArguments().getString("campSiteName"));
            tvDates.setText(getArguments().getString("bookingDates"));
            tvTotal.setText(PriceFormat.formatUsd(getArguments().getDouble("totalAmount")));
            tvErrorCode.setText(getArguments().getString("errorCode"));
            tvErrorMessage.setText(getArguments().getString("errorMessage"));
            tvTransactionId.setText(getArguments().getString("transactionId"));
        }
    }

    private void onClickListener() {
        btnBackHome.setOnClickListener(v -> {
            navController.navigate(
                    R.id.campSiteFragment,
                    null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.app_graph, true)
                            .build()
            );
        });

        btnTryAgain.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("bookingId", Long.parseLong(tvBookingId.getText().toString()));
            navController.navigate(R.id.bookingDetailFragment, args,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.app_graph, true) // clear the back stack
                            .build()
            );
        });
    }

}
