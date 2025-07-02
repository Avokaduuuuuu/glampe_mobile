package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CampTypePriceAdapter;
import com.avocado.glampe_mobile.adapter.SelectionPriceAdapter;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;
import com.avocado.glampe_mobile.model.dto.bookingdetail.resp.BookingDetailResponse;
import com.avocado.glampe_mobile.model.dto.bookingdetail.resp.CombinedBookingDetailResponse;
import com.avocado.glampe_mobile.viewModel.BookingViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDetailFragment extends Fragment {

    ImageView btnBack;
    AppCompatButton btnContinueToPayment;
    TextView tvCampSiteName, tvAddress, tvBookingDate, tvTotalPrice;
    AppCompatEditText etFullName, etEmail, etPhone;
    NavController navController;
    LottieAnimationView loadingAnimation;
    NestedScrollView nestedScrollView;


    private BookingResponse bookingResponse;
    private BookingViewModel bookingViewModel;
    private Long bookingId;

    private RecyclerView rvCampTypes, rvSelections;
    private CampTypePriceAdapter campTypePriceAdapter;
    private SelectionPriceAdapter selectionPriceAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        onClickListener();
        initViewModel();
        observeFetchBookingById();
        fetchBookingById();
    }

    private void initView(View view){
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
    }

    private void initViewModel() {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
    }

    private void updateView() {
        tvCampSiteName.setText(bookingResponse.getCampSite().getName());
        tvAddress.setText(bookingResponse.getCampSite().getAddress());
        tvBookingDate.setText(formatDateRange(bookingResponse.getCheckInAt(), bookingResponse.getCheckOutAt()));
        etFullName.setText(bookingResponse.getUser().getLastName() + " " + bookingResponse.getUser().getLastName());
        etEmail.setText(bookingResponse.getUser().getEmail());
        etPhone.setText(bookingResponse.getUser().getPhoneNumber());

        setUpAdapter();
    }

    private void setUpAdapter() {
        rvCampTypes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        campTypePriceAdapter = new CampTypePriceAdapter(combineBookingsDetails());
        rvCampTypes.setAdapter(campTypePriceAdapter);

        rvSelections.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        selectionPriceAdapter = new SelectionPriceAdapter(bookingResponse.getBookingSelections());
        rvSelections.setAdapter(selectionPriceAdapter);
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


    private void onClickListener() {
        btnBack.setOnClickListener(v -> {
            navController.navigate(R.id.action_bookingDetailFragment_to_bookingFragment);
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
            if (isLoading != null) {
                if (isLoading) {
                    loadingAnimation.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
            }
        });

        bookingViewModel.getFetchedByIdBooking().observe(getViewLifecycleOwner(), booking -> {
            if (booking != null) {
                loadingAnimation.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                this.bookingResponse = booking;
                updateView();
            }
        });
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
