package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avocado.glampe_mobile.BuildConfig;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CampTypePriceAdapter;
import com.avocado.glampe_mobile.adapter.SelectionPriceAdapter;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;
import com.avocado.glampe_mobile.model.dto.bookingdetail.resp.BookingDetailResponse;
import com.avocado.glampe_mobile.model.dto.bookingdetail.resp.CombinedBookingDetailResponse;
import com.avocado.glampe_mobile.model.dto.payment.req.PaymentIntentRequest;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.avocado.glampe_mobile.viewModel.BookingViewModel;
import com.avocado.glampe_mobile.viewModel.PaymentViewModel;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.payments.paymentlauncher.PaymentLauncher;
import com.stripe.android.payments.paymentlauncher.PaymentResult;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.model.ConfirmPaymentIntentParams;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookingDetailFragment extends Fragment {

    ImageView btnBack;
    AppCompatButton btnContinueToPayment;
    TextView tvCampSiteName, tvAddress, tvBookingDate, tvTotalPrice;
    AppCompatEditText etFullName, etEmail, etPhone;
    NavController navController;
    LottieAnimationView loadingAnimation;
    NestedScrollView nestedScrollView;
    FrameLayout loadingOverlay;
    CardInputWidget cardInputWidget;
    LinearLayout layoutPayment;

    private BookingResponse bookingResponse;
    private BookingViewModel bookingViewModel;
    private PaymentViewModel paymentViewModel;
    private Long bookingId;
    private BigDecimal total;

    private RecyclerView rvCampTypes, rvSelections;
    private CampTypePriceAdapter campTypePriceAdapter;
    private SelectionPriceAdapter selectionPriceAdapter;

    private PaymentLauncher paymentLauncher;

    private String clientSecret;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PaymentConfiguration.init(requireContext(), BuildConfig.STRIPE_PUBLISH_KEY);
        return inflater.inflate(R.layout.fragment_booking_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        initView(view);
        initViewModel();
        setupStripePaymentLauncher();
        onClickListener();
        observeFetchBookingById();
        fetchBookingById();
    }

    private void initView(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnContinueToPayment = view.findViewById(R.id.btnContinueToPayment);
        tvCampSiteName = view.findViewById(R.id.tvCampSiteName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvBookingDate = view.findViewById(R.id.tvBookingDate);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        navController = Navigation.findNavController(view);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        rvCampTypes = view.findViewById(R.id.rvCampTypes);
        rvSelections = view.findViewById(R.id.rvSelections);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        cardInputWidget = view.findViewById(R.id.cardInputWidget);
        layoutPayment = view.findViewById(R.id.layoutPayment);
    }

    private void initViewModel() {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
    }

    private void setupStripePaymentLauncher() {
        paymentLauncher = PaymentLauncher.Companion.create(
                this,
                BuildConfig.STRIPE_PUBLISH_KEY,
                paymentResult -> {
                    if (paymentResult instanceof PaymentResult.Completed) {
                        observerProcessCharge();
                        paymentViewModel.processCharge(getPaymentIntentIdFromClientSecret(clientSecret));
                        Toast.makeText(requireContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                    } else if (paymentResult instanceof PaymentResult.Canceled) {
                        Toast.makeText(requireContext(), "Payment canceled", Toast.LENGTH_SHORT).show();
                    } else if (paymentResult instanceof PaymentResult.Failed) {
                        Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private String getPaymentIntentIdFromClientSecret(String clientSecret) {
        if (clientSecret != null && clientSecret.startsWith("pi_")) {
            int secretIndex = clientSecret.indexOf("_secret");
            if (secretIndex != -1) {
                return clientSecret.substring(0, secretIndex);
            }
        }
        return null;
    }


    private void onClickListener() {
        btnBack.setOnClickListener(v -> navController.navigate(R.id.action_bookingDetailFragment_to_bookingFragment));

        btnContinueToPayment.setOnClickListener(v -> {
            if (validateCardInput()) {
                startPaymentFlow();
            }
        });
    }

    private boolean validateCardInput() {
        return cardInputWidget.getPaymentMethodCard() != null;
    }

    private void startPaymentFlow() {
        observerPaymentFlow();
        paymentViewModel.createIntent(
                PaymentIntentRequest.builder()
                        .bookingId(bookingId)
                        .amount(total)
                        .currency("usd")
                        .method("card")
                        .build()
        );
    }

    private void observerPaymentFlow() {
        paymentViewModel.getCreateIntentLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                loadingOverlay.setVisibility(View.VISIBLE);
            }
        });

        paymentViewModel.getPaymentIntent().observe(getViewLifecycleOwner(), paymentIntent -> {
            if (paymentIntent != null) {
                Toast.makeText(requireContext(), "Create Intent Successfully", Toast.LENGTH_SHORT).show();
                clientSecret = paymentIntent.getClientSecret();
                confirmPayment(paymentIntent.getClientSecret());
            }
        });

        paymentViewModel.getCreateIntentError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmPayment(String clientSecret) {
        PaymentMethodCreateParams.Card paymentMethodCard = cardInputWidget.getPaymentMethodCard();
        if (paymentMethodCard == null) {
            Toast.makeText(requireContext(), "Invalid card details", Toast.LENGTH_SHORT).show();
            return;
        }

        PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.create(paymentMethodCard, null);
        com.stripe.android.model.ConfirmPaymentIntentParams confirmParams = com.stripe.android.model.ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(paymentMethodParams, clientSecret);
        paymentLauncher.confirm(confirmParams);
    }

    private void observerProcessCharge() {
        paymentViewModel.getProcessChargeLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                loadingOverlay.setVisibility(View.VISIBLE);
            }
        });

        paymentViewModel.getPaymentResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                navController.navigate(R.id.action_bookingDetailFragment_to_paymentSuccessFragment);
            }
        });

        paymentViewModel.getProcessChargeError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_bookingDetailFragment_to_paymentFailFragment);
            }
        });
    }

    private void fetchBookingById() {
        if (getArguments() != null) {
            bookingId = getArguments().getLong("bookingId");
            bookingViewModel.fetchBookingById(bookingId);
        }
    }

    private void observeFetchBookingById() {
        bookingViewModel.getIsLoadingFetchById().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                loadingAnimation.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }
        });

        bookingViewModel.getFetchedByIdBooking().observe(getViewLifecycleOwner(), booking -> {
            if (booking != null) {
                loadingAnimation.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                bookingResponse = booking;
                if (booking.getStatus().equals("Pending")) layoutPayment.setVisibility(View.VISIBLE);
                updateView();
            }
        });
    }

    private void updateView() {
        tvCampSiteName.setText(bookingResponse.getCampSite().getName());
        tvAddress.setText(bookingResponse.getCampSite().getAddress());
        tvBookingDate.setText(formatDateRange(bookingResponse.getCheckInAt(), bookingResponse.getCheckOutAt()));
        etFullName.setText(bookingResponse.getUser().getLastName() + " " + bookingResponse.getUser().getFirstName());
        etEmail.setText(bookingResponse.getUser().getEmail());
        etPhone.setText(bookingResponse.getUser().getPhoneNumber());

        setUpAdapter();
    }

    private void setUpAdapter() {
        List<CombinedBookingDetailResponse> combinedBookingDetailResponses = combineBookingsDetails();
        rvCampTypes.setLayoutManager(new LinearLayoutManager(requireContext()));
        campTypePriceAdapter = new CampTypePriceAdapter(combinedBookingDetailResponses);
        rvCampTypes.setAdapter(campTypePriceAdapter);

        rvSelections.setLayoutManager(new LinearLayoutManager(requireContext()));
        selectionPriceAdapter = new SelectionPriceAdapter(bookingResponse.getBookingSelections());
        rvSelections.setAdapter(selectionPriceAdapter);

        total = combinedBookingDetailResponses.stream().map(CombinedBookingDetailResponse::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(
                        bookingResponse.getBookingSelections().stream()
                                .map(bs -> bs.getSelection().getPrice().multiply(BigDecimal.valueOf(bs.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                );

        tvTotalPrice.setText(PriceFormat.formatUsd(total.doubleValue()));
    }

    private List<CombinedBookingDetailResponse> combineBookingsDetails() {
        Map<Long, CombinedBookingDetailResponse> map = new HashMap<>();

        for (BookingDetailResponse bd : bookingResponse.getBookingDetails()) {
            long campTypeId = bd.getCampType().getId();

            map.compute(campTypeId, (id, existing) -> {
                if (existing == null) {
                    return CombinedBookingDetailResponse.builder()
                            .campTypeResponse(bd.getCampType())
                            .quantity(1)
                            .totalAmount(bd.getAmount())
                            .build();
                } else {
                    existing.setQuantity(existing.getQuantity() + 1);
                    existing.setTotalAmount(existing.getTotalAmount().add(bd.getAmount()));
                    return existing;
                }
            });
        }

        return new ArrayList<>(map.values());
    }

    public static String formatDateRange(LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern("MMM d");
        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

        if (start.getYear() == end.getYear()) {
            return String.format("%s – %s, %d",
                    start.format(monthDayFormatter),
                    end.format(monthDayFormatter),
                    start.getYear());
        } else {
            return String.format("%s – %s",
                    start.format(fullFormatter),
                    end.format(fullFormatter));
        }
    }
}
