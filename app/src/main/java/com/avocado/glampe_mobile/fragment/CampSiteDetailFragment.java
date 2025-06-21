package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CampTypeAdapter;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.viewModel.CampSiteDetailViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CampSiteDetailFragment extends Fragment implements TripCalendarBottomSheet.OnDatesSelectedListener {

    ImageButton imageButton;
    NavController navController;
    MaterialButton btnAbout, btnOption, btnReview;
    List<MaterialButton> buttons;
    ViewFlipper viewFlipper;
    ShapeableImageView campSiteImage;
    TextView tvCheckInDate, tvTotal, tvDescription, tvCampSiteName, tvCampSiteAddress;
    LinearLayout layoutDate;
    LottieAnimationView loadingAnimation;
    RecyclerView recyclerViewCampType;
    NestedScrollView nestedScrollView;

    private LocalDate checkInDate = LocalDate.now(), checkOutDate = LocalDate.now();
    private int totalNights = 0;
    private BigDecimal total = BigDecimal.ZERO;
    private long weekdays = 0;
    private long weekend = 0;


    private CampSiteResponse campSiteResponse;
    private Map<Integer, Integer> selectedQuantity = new HashMap<>();
    private CampTypeAdapter campTypeAdapter;
    private List<CampTypeResponse> campTypeResponses = new ArrayList<>();
    private CampSiteDetailViewModel campSiteDetailViewModel;

    private Long campSiteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camp_site_detail, container ,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initialize(view);
        initViewModels();
        getCampSiteId();
        fetchData();
        observeViewModel();

        imageButton.setOnClickListener(v -> navController.popBackStack());
    }

    private void getCampSiteId(){
        if (getArguments() != null){
            campSiteId = getArguments().getLong("campSiteId", -1L);
        }
    }

    private void initialize(View view){
        tvCampSiteName = view.findViewById(R.id.tvCampSiteName);
        tvCampSiteAddress = view.findViewById(R.id.tvCampSiteAddress);
        tvDescription = view.findViewById(R.id.tvDescription);
        imageButton = view.findViewById(R.id.backButton);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnOption = view.findViewById(R.id.btnOption);
        btnReview = view.findViewById(R.id.btnReview);
        navController = Navigation.findNavController(view);
        viewFlipper = view.findViewById(R.id.viewFlipper);
        tvCheckInDate = view.findViewById(R.id.check_in_date);
        tvCheckInDate.setText("Select dates");
        layoutDate = view.findViewById(R.id.date_layout);
        recyclerViewCampType = view.findViewById(R.id.recyclerViewCampType);
        tvTotal = view.findViewById(R.id.tvTotal);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        nestedScrollView = view.findViewById(R.id.scrollView);
        if (selectedQuantity.isEmpty()) tvTotal.setText(PriceFormat.formatUsd(0.0));
    }

    private void initViewModels(){
        campSiteDetailViewModel = new ViewModelProvider(this).get(CampSiteDetailViewModel.class);
    }

    private void fetchData(){
        if (campSiteId > 0) {
            campSiteDetailViewModel.loadById(campSiteId);
        }
    }

    private void observeViewModel(){

        campSiteDetailViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                loadingAnimation.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }
        });

        campSiteDetailViewModel.getCampSite().observe(getViewLifecycleOwner(), campsite -> {
            if (campsite != null) {
                campSiteResponse = campsite;
                loadingAnimation.setVisibility(View.GONE);
                updateView();
            }
        });
    }

    private void updateView(){
        setUpButtons();
        setUpCalendar();
        setUpCampTypeAdapter();
        tvCampSiteName.setText(campSiteResponse.getName());
        tvCampSiteAddress.setText(campSiteResponse.getAddress());
        tvDescription.setText(campSiteResponse.getDescription());
        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void setUpButtons() {
        buttons = List.of(btnAbout, btnOption, btnReview);

        // Define what each button index maps to in the ViewFlipper
        for (int i = 0; i < buttons.size(); i++) {
            final int index = i;
            MaterialButton button = buttons.get(i);

            button.setOnClickListener(v -> {
                // Set view flipper to correct view
                viewFlipper.setDisplayedChild(index);


                // Update selected state for all buttons
                for (MaterialButton btn : buttons) {
                    btn.setSelected(false);
                }
                button.setSelected(true);
            });
        }

        // Set default selection
        btnAbout.performClick();
    }

    private void setUpCalendar(){
        layoutDate.setOnClickListener(v -> showTripCalendar());
    }

    private void showTripCalendar() {
        TripCalendarBottomSheet dialog = TripCalendarBottomSheet.newInstance(
                checkInDate, // Current check-in date
                checkOutDate, // Current check-out date
                this  // OnDatesSelectedListener
        );

        dialog.show(getChildFragmentManager(), "TripCalendarDialog");
    }

    @Override
    public void onDatesSelected(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            updateBookingDates(startDate, endDate);
            this.checkInDate = startDate;
            this.checkOutDate = endDate;
            this.totalNights = (int) ChronoUnit.DAYS.between(startDate, endDate);
            this.weekend = countWeekends(startDate, endDate);
            this.weekdays = this.totalNights - this.weekend;

            selectedQuantity.clear();
            if (campTypeAdapter != null) campTypeAdapter.resetQuantities();
            updateTotal();
        }
    }

    private void updateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        String checkInDate = checkIn.format(formatter);
        String checkOutDate = checkOut.format(formatter);
        String date = checkInDate + " - " + checkOutDate;
        tvCheckInDate.setText(date);

    }

    private void setUpCampTypeAdapter(){
        campTypeResponses = campSiteResponse.getCampTypes();
        recyclerViewCampType.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        campTypeAdapter = new CampTypeAdapter(campTypeResponses);
        recyclerViewCampType.setAdapter(campTypeAdapter);
        campTypeAdapter.setOnCampTypeListener(new CampTypeAdapter.OnCampTypeListener() {
            @Override
            public void onQuantityChanged(CampTypeResponse campType, int newQuantity) {
                selectedQuantity.put(campType.getId(), newQuantity);
                Log.i("selected quantity", selectedQuantity.toString());
                updateTotal();
            }

            @Override
            public void onCampTypeSelected(CampTypeResponse campType) {

            }

            @Override
            public void onViewDetailsClicked(CampTypeResponse campType) {

            }
        });
    }


    private Long countWeekends(LocalDate checkIn, LocalDate checkOut) {
        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
            return 0L;
        }

        long count = 0L;
        LocalDate currentDate = checkIn; // Don't modify the parameter

        while (currentDate.isBefore(checkOut)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // Count actual weekends (Saturday and Sunday)
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                count++;
            }

            currentDate = currentDate.plusDays(1); // ✅ FIXED! Assign the result back
        }

        return count;
    }

    private void updateTotal() {
        // Reset total before calculation
        if (!selectedQuantity.isEmpty()) {
            total = BigDecimal.ZERO; // ✅ FIXED! Reset total first

            Map<Integer, CampTypeResponse> campTypeMap = campTypeResponses.stream()
                    .collect(Collectors.toMap(CampTypeResponse::getId, Function.identity()));

            selectedQuantity.forEach((key, value) -> {
                if (value > 0) { // Only calculate if quantity > 0
                    CampTypeResponse campType = campTypeMap.get(key);
                    if (campType != null) {
                        Log.d("campType: ", campType.toString());

                        BigDecimal totalWeekDays = BigDecimal.valueOf(campType.getPrice() * weekdays * value);
                        Log.d("total weekdays:", totalWeekDays.toString());

                        BigDecimal totalWeekend = BigDecimal.valueOf(campType.getWeekendPrice() * weekend * value);
                        Log.d("total weekend:", totalWeekend.toString());

                        total = total.add(totalWeekDays).add(totalWeekend); // ✅ FIXED! Cleaner addition
                    }
                }
            });

            Log.d("total: ", total.toString());
            tvTotal.setText(PriceFormat.formatUsd(total.doubleValue()));
        } else tvTotal.setText(PriceFormat.formatUsd(0.0));
    }
}
