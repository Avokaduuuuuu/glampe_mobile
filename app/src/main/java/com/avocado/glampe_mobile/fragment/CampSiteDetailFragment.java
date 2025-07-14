package com.avocado.glampe_mobile.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.activity.AuthActivity;
import com.avocado.glampe_mobile.adapter.CampTypeAdapter;
import com.avocado.glampe_mobile.adapter.ImageSliderAdapter;
import com.avocado.glampe_mobile.adapter.SelectionAdapter;
import com.avocado.glampe_mobile.adapter.UtilityAdapter;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.di.CampSiteCartManager;
import com.avocado.glampe_mobile.di.DateHelper;
import com.avocado.glampe_mobile.model.dto.booking.req.BookingRequest;
import com.avocado.glampe_mobile.model.dto.bookingdetail.req.BookingDetailRequest;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.dto.camptype.filter.CampTypeFilterParams;
import com.avocado.glampe_mobile.model.dto.gallery.GalleryResponse;
import com.avocado.glampe_mobile.model.dto.selection.req.BookingSelectionRequest;
import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.model.dto.utility.resp.UtilityResponse;
import com.avocado.glampe_mobile.model.entity.CartItem;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.entity.SelectedAddon;
import com.avocado.glampe_mobile.model.entity.SelectedCampType;
import com.avocado.glampe_mobile.viewModel.BookingViewModel;
import com.avocado.glampe_mobile.viewModel.CampSiteDetailViewModel;
import com.avocado.glampe_mobile.viewModel.CampTypeViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class CampSiteDetailFragment extends Fragment implements TripCalendarBottomSheet.OnDatesSelectedListener, OnMapReadyCallback {

    ImageButton imageButton;
    NavController navController;
    MaterialButton btnAbout, btnOption, btnReview, btnBookNow, btnChatOwner;
    List<MaterialButton> buttons;
    ViewFlipper viewFlipper;
    ShapeableImageView campSiteImage;
    TextView tvCheckInDate, tvPricePerNight, tvAddOnTotal, tvTotal, tvDescription, tvCampSiteName, tvCampSiteAddress, tvOwnerName;
    LinearLayout layoutDate;
    LottieAnimationView loadingAnimation, loadingCampType;
    RecyclerView recyclerViewCampType, recyclerViewUtility, recyclerViewSelection;
    NestedScrollView nestedScrollView;
    MapView mapView;
    ViewPager2 viewPager2;
    FrameLayout loadingOverlay;

    private LocalDate checkInDate = LocalDate.now(), checkOutDate = LocalDate.now();
    private int totalNights = 0;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal addOnTotal = BigDecimal.ZERO;
    private BigDecimal pricePerNight = BigDecimal.ZERO;
    private long weekdays = 0;
    private long weekend = 0;


    private CampSiteResponse campSiteResponse;
    private Map<Long, Integer> selectedQuantity = new HashMap<>();
    private Map<Long, Integer> selectionSelectedQuantity = new HashMap<>();
    private CampTypeAdapter campTypeAdapter;
    private UtilityAdapter utilityAdapter;
    private SelectionAdapter selectionAdapter;
    private ImageSliderAdapter imageSliderAdapter;
    private List<CampTypeResponse> campTypeResponses = new ArrayList<>();
    private List<UtilityResponse> utilityResponses = new ArrayList<>();
    private CampSiteDetailViewModel campSiteDetailViewModel;
    private CampTypeViewModel campTypeViewModel;
    private BookingViewModel bookingViewModel;

    private Long campSiteId;
    private AuthUserResponse authUser;

    private CampSiteCartManager cartManager;
    private MaterialButton btnCart;

    private boolean isDataReady = false;
    private boolean shouldLoadSavedSelections = true;
    private boolean savedSelectionsDataLoaded = false; // Track if data is loaded but adapters not ready

    private Map<Long, Integer> pendingSavedCampTypes = new HashMap<>();
    private Map<Long, Integer> pendingSavedSelections = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camp_site_detail, container ,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Window window = requireActivity().getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        initialize(view);
        initViewModels();
        getCampSiteId();
        fetchData();
        observeViewModel();
        mapView.onCreate(savedInstanceState);
        onClickListener();
        initCartManager();
    }

    private void initCartManager() {
        cartManager = CampSiteCartManager.getInstance(requireContext());
        Log.d("CampSiteDetail", "🛒 Cart manager initialized");
    }

    private void getCampSiteId(){
        if (getArguments() != null){
            campSiteId = getArguments().getLong("campSiteId", -1L);
        }
    }


    private boolean isUserAuthenticated() {
        // Option 1: Sử dụng AuthManager (recommend vì fragment đã dùng)
        try {
            AuthUserResponse authResponse = AuthManager.getAuthResponse(requireContext());
            return authResponse != null && authResponse.getUser() != null;
        } catch (Exception e) {
            Log.e("CampSiteDetail", "Error checking auth", e);
            return false;
        }

    }

    // Method để show login required dialog
    private void showLoginRequiredDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_login_required, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton btnLogin = dialogView.findViewById(R.id.btnLogin);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnLogin.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate to AuthActivity
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            startActivity(intent);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }



    private void onClickListener(){
        imageButton.setOnClickListener(v -> navController.navigate(R.id.campSiteFragment, null,
                new NavOptions.Builder()
                        .setPopUpTo(R.id.app_graph, true)
                        .build()));
        btnBookNow.setOnClickListener(v -> {
            if (!isUserAuthenticated()) {
                showLoginRequiredDialog();
                return;
            }
            List<BookingDetailRequest> bookingDetailRequests = new ArrayList<>();
            List<BookingSelectionRequest> bookingSelectionRequests = new ArrayList<>();

            selectedQuantity.forEach((key, value) -> {
                bookingDetailRequests.add(BookingDetailRequest.builder()
                                .campTypeId(key.longValue())
                                .quantity(value)
                        .build());
            });

            selectionSelectedQuantity.forEach((key, value) -> {
                bookingSelectionRequests.add(BookingSelectionRequest.builder()
                                .selectionId(key.longValue())
                                .quantity(value)
                        .build());
            });

            BookingRequest bookingRequest = BookingRequest.builder()
                    .userId(authUser.getUser().getId())
                    .campSiteId(campSiteId)
                    .checkInTime(checkInDate.atStartOfDay())
                    .checkOutTime(checkOutDate.atStartOfDay())
                    .bookingDetailRequests(bookingDetailRequests)
                    .bookingSelectionRequests(bookingSelectionRequests)
                    .totalAmount(total)
                    .build();

            observerAddBooking();
            bookingViewModel.addBooking(bookingRequest);

            Log.d("Booking Request", bookingRequest.toString());
        });
        btnChatOwner.setOnClickListener(v -> {
            if (!isUserAuthenticated()) {
                showLoginRequiredDialog();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putLong("recipientId", campSiteResponse.getUser().getId());
            bundle.putString("recipientName", campSiteResponse.getUser().getFirstName());
            navController.navigate(R.id.action_campSiteDetailFragment_to_chatFragment, bundle);
        });
        btnCart.setOnClickListener(v -> {
            Log.d("CampSiteDetail", "🛒 Cart button clicked");
            saveCurrentSelectionsToCart();
        });
    }

    private void saveCurrentSelectionsToCart() {
        if (campSiteResponse == null) {
            Toast.makeText(requireContext(), "Dữ liệu địa điểm chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if we have dates selected
        if (checkInDate == null || checkOutDate == null || totalNights <= 0) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày cắm trại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if we have any selections
        if (selectedQuantity.isEmpty() && selectionSelectedQuantity.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn loại lều hoặc dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convert selected camp types
            List<CampTypeResponse> selectedCampTypes = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : selectedQuantity.entrySet()) {
                Long campTypeId = entry.getKey();
                Integer quantity = entry.getValue();

                if (quantity > 0) {
                    CampTypeResponse campType = campTypeResponses.stream()
                            .filter(ct -> ct.getId().equals(campTypeId))
                            .findFirst()
                            .orElse(null);

                    if (campType != null) {
                        // Create a copy with selected quantity
                        CampTypeResponse copy = createCampTypeCopy(campType);
                        copy.setSelectedQuantity(quantity);
                        selectedCampTypes.add(copy);
                    }
                }
            }

            // Convert selected addons
            List<SelectionResponse> selectedAddons = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : selectionSelectedQuantity.entrySet()) {
                Long selectionId = entry.getKey();
                Integer quantity = entry.getValue();

                if (quantity > 0) {
                    SelectionResponse selection = campSiteResponse.getSelections().stream()
                            .filter(s -> s.getId().equals(selectionId))
                            .findFirst()
                            .orElse(null);

                    if (selection != null) {
                        SelectionResponse copy = createSelectionCopy(selection);
                        copy.setSelectedQuantity(quantity);
                        selectedAddons.add(copy);
                    }
                }
            }

            // Convert dates to millis
            Long checkInMillis = DateHelper.localDateToMillis(checkInDate);
            Long checkOutMillis = DateHelper.localDateToMillis(checkOutDate);

            // Get campsite image
            String campsiteImage = null;
            if (campSiteResponse.getGalleries() != null && !campSiteResponse.getGalleries().isEmpty()) {
                campsiteImage = campSiteResponse.getGalleries().get(0).getPath();
            }

            // Save to cart using cart manager
            cartManager.saveCurrentSelection(
                    campSiteResponse.getId(),
                    campSiteResponse.getName(),
                    campsiteImage,
                    campSiteResponse.getAddress(),
                    checkInMillis,
                    checkOutMillis,
                    selectedCampTypes,
                    selectedAddons
            );

            // Show success message with animation
            Toast.makeText(requireContext(),
                    "✅ Đã thêm " + campSiteResponse.getName() + " vào giỏ hàng",
                    Toast.LENGTH_SHORT).show();

            // Add visual feedback
            animateCartButton();

            Log.d("CampSiteDetail", "🛒 Successfully added to cart: " +
                    selectedCampTypes.size() + " camp types, " +
                    selectedAddons.size() + " add-ons, " +
                    totalNights + " nights");

        } catch (Exception e) {
            Log.e("CampSiteDetail", "❌ Error adding to cart", e);
            Toast.makeText(requireContext(), "Có lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private CampTypeResponse createCampTypeCopy(CampTypeResponse original) {
        CampTypeResponse copy = new CampTypeResponse();
        copy.setId(original.getId());
        copy.setType(original.getType());
        copy.setCapacity(original.getCapacity());
        copy.setPrice(original.getPrice());
        copy.setWeekendPrice(original.getWeekendPrice());
        copy.setQuantity(original.getQuantity());
        copy.setIsDeleted(original.getIsDeleted());
        copy.setCampSiteId(original.getCampSiteId());
        copy.setImage(original.getImage());
        copy.setFacilities(original.getFacilities());
        if (original.getWeekendPrice() != null) {
            copy.setWeekendPrice(original.getWeekendPrice());
        }
        copy.setUpdatedAt(original.getUpdatedAt());
        return copy;
    }

    private SelectionResponse createSelectionCopy(SelectionResponse original) {
        SelectionResponse copy = new SelectionResponse();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setPrice(original.getPrice());
        copy.setImage(original.getImage());
        copy.setIsDeleted(original.getIsDeleted());
        return copy;
    }

    private void animateCartButton() {
        // Scale and color animation
        btnCart.animate()
                .scaleX(1.1f).scaleY(1.1f)
                .setDuration(150)
                .withEndAction(() -> {
                    btnCart.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(150);
                });

        // Temporary background color change
        ColorStateList originalBg = btnCart.getBackgroundTintList();
        ColorStateList originalIcon = btnCart.getIconTint();

        // Change to success colors temporarily
        btnCart.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.success));
        btnCart.setIconTint(ContextCompat.getColorStateList(requireContext(), android.R.color.white));

        // Revert after animation
        btnCart.postDelayed(() -> {
            btnCart.setBackgroundTintList(originalBg);
            btnCart.setIconTint(originalIcon);
        }, 500);
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
        tvPricePerNight = view.findViewById(R.id.tvPricePerNight);
        tvAddOnTotal = view.findViewById(R.id.tvAddonTotal);
        tvTotal = view.findViewById(R.id.tvTotalPrice);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        nestedScrollView = view.findViewById(R.id.scrollView);
        recyclerViewUtility = view.findViewById(R.id.utilityRecyclerView);
        mapView = view.findViewById(R.id.mapView);
        viewPager2 = view.findViewById(R.id.viewPager);
        loadingCampType = view.findViewById(R.id.loadingCampType);
        recyclerViewSelection = view.findViewById(R.id.recyclerViewSelection);
        btnBookNow = view.findViewById(R.id.btnBookNow);
        authUser = AuthManager.getAuthResponse(requireContext());
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        if (selectedQuantity.isEmpty() && selectionSelectedQuantity.isEmpty()) {
            tvTotal.setText(PriceFormat.formatUsd(0.0));
            tvAddOnTotal.setText(PriceFormat.formatUsd(0.0));
            tvPricePerNight.setText(PriceFormat.formatUsd(0.0));
            updateBtnState();
        }
        tvOwnerName = view.findViewById(R.id.tvOwnerName);
        btnChatOwner = view.findViewById(R.id.btnChatOwner);
        btnCart = view.findViewById(R.id.btnCart);
    }

    private void initViewModels(){
        campSiteDetailViewModel = new ViewModelProvider(this).get(CampSiteDetailViewModel.class);
        campTypeViewModel = new ViewModelProvider(this).get(CampTypeViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
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
                mapView.getMapAsync(this);
                updateView();

                // Check and load saved selections after campsite data loaded
                checkAndLoadSavedSelections();
            }
        });
    }

    private void observeCampTypeViewModel(){
        campTypeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                loadingCampType.setVisibility(View.VISIBLE);
                recyclerViewCampType.setVisibility(View.GONE);
            }
        });

        campTypeViewModel.getCampTypes().observe(getViewLifecycleOwner(), campTypes -> {
            if (campTypes != null) {
                campTypeResponses = campTypes;
                recyclerViewCampType.setVisibility(View.VISIBLE);
                loadingCampType.setVisibility(View.GONE);
                setUpCampTypeAdapter();
                setUpSelectionAdapter();

                // Check and load saved selections after camp types loaded
                applyPendingSavedSelections();
            }
        });
    }

    private void checkAndLoadSavedSelections() {
        if (shouldLoadSavedSelections && campSiteResponse != null) {
            Log.d("CampSiteDetail", "🔄 All data ready, loading saved cart selections");
            loadSavedCartSelections();
            shouldLoadSavedSelections = false; // Prevent multiple loads
        }
    }
    private void observerAddBooking() {
        bookingViewModel.getIsLoadingAdd().observe(getViewLifecycleOwner(), isLoadingAdd -> {
            if (isLoadingAdd != null) {
                if (isLoadingAdd) {
                    loadingOverlay.setVisibility(View.VISIBLE);
                    btnBookNow.setEnabled(false);
                }
            }
        });

        bookingViewModel.getBooking().observe(getViewLifecycleOwner(), booking -> {
            if (booking != null) {
                Toast.makeText(requireContext(), "Adding booking successfully", Toast.LENGTH_SHORT).show();
                Log.d("CampSiteDetailFragment", "Adding booking successfully: " + booking);
                loadingOverlay.setVisibility(View.GONE);
                btnBookNow.setEnabled(true);

                Bundle bundle = new Bundle();
                bundle.putLong("bookingId", booking.getId());
                navController.navigate(R.id.action_campSiteDetailFragment_to_bookingDetailFragment, bundle);
            }
        });

        bookingViewModel.getErrorAdd().observe(getViewLifecycleOwner(), errorAdd -> {
            if (errorAdd != null) {
                Toast.makeText(requireContext(), errorAdd, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateView(){
        setUpButtons();
        setUpCalendar();
        setUpUtilityAdapter();
        setUpViewPager();
        tvCampSiteName.setText(campSiteResponse.getName());
        tvCampSiteAddress.setText(campSiteResponse.getAddress());
        tvDescription.setText(campSiteResponse.getDescription());
        nestedScrollView.setVisibility(View.VISIBLE);
        tvOwnerName.setText(campSiteResponse.getUser().getFirstName());
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
            selectionSelectedQuantity.clear();
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
        campTypeViewModel.fetchAllCampTypes(
                CampTypeFilterParams.builder()
                        .campSiteId(campSiteResponse.getId())
                        .checkInAt(checkIn)
                        .checkOutAt(checkOut)
                        .build()
        );
        observeCampTypeViewModel();
    }


    private void setUpCampTypeAdapter(){
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

    private void setUpSelectionAdapter() {
        Log.d("Selecions", campSiteResponse.getSelections().toString());
        recyclerViewSelection.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        selectionAdapter = new SelectionAdapter(campSiteResponse.getSelections());
        recyclerViewSelection.setAdapter(selectionAdapter);
        selectionAdapter.setOnSelectionListener(new SelectionAdapter.OnSelectionListener() {
            @Override
            public void onQuantityChanged(SelectionResponse selectionResponse, int newQuantity) {
                selectionSelectedQuantity.put(selectionResponse.getId(), newQuantity);
                updateTotal();
            }

            @Override
            public void onSelectionSelected(SelectionResponse selectionResponse) {

            }
        });
    }

    private void setUpUtilityAdapter(){
        utilityResponses = campSiteResponse.getUtilities();
        recyclerViewUtility.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        utilityAdapter = new UtilityAdapter(utilityResponses);
        recyclerViewUtility.setAdapter(utilityAdapter);
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
        if (!selectedQuantity.isEmpty() || !selectionSelectedQuantity.isEmpty()) {
            total = BigDecimal.ZERO; // ✅ FIXED! Reset total first
            pricePerNight = BigDecimal.ZERO;
            addOnTotal = BigDecimal.ZERO;

            updateBtnState();

            Map<Long, CampTypeResponse> campTypeMap = campTypeResponses.stream()
                    .collect(Collectors.toMap(CampTypeResponse::getId, Function.identity()));

            Map<Long, SelectionResponse> selectionMap = campSiteResponse.getSelections().stream()
                            .collect(Collectors.toMap(SelectionResponse::getId, Function.identity()));

            selectedQuantity.forEach((key, value) -> {
                if (value > 0) { // Only calculate if quantity > 0
                    CampTypeResponse campType = campTypeMap.get(key);
                    if (campType != null) {
                        Log.d("campType: ", campType.toString());

                        BigDecimal totalWeekDays = BigDecimal.valueOf(campType.getPrice() * weekdays * value);
                        Log.d("total weekdays:", totalWeekDays.toString());

                        BigDecimal totalWeekend = BigDecimal.valueOf(campType.getWeekendPrice() * weekend * value);
                        Log.d("total weekend:", totalWeekend.toString());

                        pricePerNight = pricePerNight.add(totalWeekDays).add(totalWeekend); // ✅ FIXED! Cleaner addition
                    }
                }
            });

            selectionSelectedQuantity.forEach((key, value) -> {
                if (value > 0) {
                    SelectionResponse selectionResponse = selectionMap.get(key);
                    if (selectionResponse != null) {
                        addOnTotal = addOnTotal.add(BigDecimal.valueOf(value * selectionResponse.getPrice().doubleValue()));
                    }
                }
            });

            total = pricePerNight.add(addOnTotal);

            Log.d("total: ", total.toString());
            tvTotal.setText(PriceFormat.formatUsd(total.doubleValue()));
            tvPricePerNight.setText(PriceFormat.formatUsd(pricePerNight.doubleValue()));
            tvAddOnTotal.setText(PriceFormat.formatUsd(addOnTotal.doubleValue()));
        } else tvTotal.setText(PriceFormat.formatUsd(0.0));
    }

    private void updateBtnState(){
        Boolean hasSelection = !selectedQuantity.isEmpty();

        btnBookNow.setEnabled(hasSelection);
        btnBookNow.setBackgroundTintList(ContextCompat.getColorStateList(
                requireContext(),
                hasSelection ? R.color.btn_book_now : R.color.disabled_gray
        ));
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (campSiteResponse != null && campSiteResponse.getLatitude() != null && campSiteResponse.getLongitude() != null) {
            LatLng campSiteLocation = new LatLng(campSiteResponse.getLatitude(), campSiteResponse.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(campSiteLocation));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campSiteLocation, 15f));
        } else {
            Log.e("MapReady", "CampSiteResponse or its location is null");
        }
    }

    private void loadSavedCartSelections() {
        if (campSiteResponse == null || cartManager == null) {
            Log.w("CampSiteDetail", "⚠️ Cannot load saved selections - missing dependencies");
            return;
        }

        try {
            Log.d("CampSiteDetail", "🔄 Loading saved cart selections for campsite: " + campSiteResponse.getName());

            // Get saved cart item for this campsite
            CartItem savedItem = cartManager.getSavedSelection(campSiteResponse.getId());
            if (savedItem == null) {
                Log.d("CampSiteDetail", "📭 No saved selections found for this campsite");
                return;
            }

            Log.d("CampSiteDetail", "🎯 Found saved cart item:");
            Log.d("CampSiteDetail", "   - Camp types: " + savedItem.getSelectedCampTypes().size());
            Log.d("CampSiteDetail", "   - Add-ons: " + savedItem.getSelectedAddons().size());

            // 1. Load saved dates if available
            if (savedItem.getCheckInDate() != null && savedItem.getCheckOutDate() != null) {
                LocalDate savedCheckIn = DateHelper.millisToLocalDate(savedItem.getCheckInDate());
                LocalDate savedCheckOut = DateHelper.millisToLocalDate(savedItem.getCheckOutDate());

                if (savedCheckIn != null && savedCheckOut != null) {
                    Log.d("CampSiteDetail", "📅 Restoring dates: " + savedCheckIn + " to " + savedCheckOut);

                    // Set dates and calculate nights
                    this.checkInDate = savedCheckIn;
                    this.checkOutDate = savedCheckOut;
                    this.totalNights = (int) ChronoUnit.DAYS.between(savedCheckIn, savedCheckOut);
                    this.weekend = countWeekends(savedCheckIn, savedCheckOut);
                    this.weekdays = this.totalNights - this.weekend;

                    // Update date display
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
                    String checkInDateStr = savedCheckIn.format(formatter);
                    String checkOutDateStr = savedCheckOut.format(formatter);
                    String dateRange = checkInDateStr + " - " + checkOutDateStr;
                    tvCheckInDate.setText(dateRange);

                    // Trigger camp types API call
                    campTypeViewModel.fetchAllCampTypes(
                            CampTypeFilterParams.builder()
                                    .campSiteId(campSiteResponse.getId())
                                    .checkInAt(savedCheckIn)
                                    .checkOutAt(savedCheckOut)
                                    .build()
                    );
                    observeCampTypeViewModel();
                }
            }

            // 2. Store saved quantities for later application (when adapters are ready)
            pendingSavedCampTypes.clear();
            for (SelectedCampType savedCampType : savedItem.getSelectedCampTypes()) {
                pendingSavedCampTypes.put(savedCampType.getCampTypeId(), savedCampType.getQuantity());
                Log.d("CampSiteDetail", "   - Stored camp type " + savedCampType.getCampTypeId() +
                        " with quantity " + savedCampType.getQuantity());
            }

            pendingSavedSelections.clear();
            for (SelectedAddon savedAddon : savedItem.getSelectedAddons()) {
                pendingSavedSelections.put(savedAddon.getSelectionId(), savedAddon.getQuantity());
                Log.d("CampSiteDetail", "   - Stored add-on " + savedAddon.getSelectionId() +
                        " with quantity " + savedAddon.getQuantity());
            }

            savedSelectionsDataLoaded = true;
            Log.d("CampSiteDetail", "📦 Saved selections data stored, waiting for adapters...");

        } catch (Exception e) {
            Log.e("CampSiteDetail", "❌ Error loading saved cart selections", e);
        }
    }

    private void updateAdaptersWithSavedSelections() {
        try {
            Log.d("CampSiteDetail", "Update saved camp types and selections");
            if (campTypeAdapter != null && !selectedQuantity.isEmpty()) {
                Log.d("CampSiteDetail", "🔄 Updating camp type adapter with saved quantities");
                campTypeAdapter.applySavedQuantities(selectedQuantity);
            } else {
                Log.d("CampSiteDetail", campTypeAdapter == null ? "CampTypeAdapter is null" : "selectedQuanity is empty");
            }

            // Update selection adapter with restored quantities
            if (selectionAdapter != null && !selectionSelectedQuantity.isEmpty()) {
                Log.d("CampSiteDetail", "🔄 Updating selection adapter with saved quantities");
                selectionAdapter.applySavedQuantities(selectionSelectedQuantity);
            }

            Log.d("CampSiteDetail", "✅ Adapters updated with saved selections");

        } catch (Exception e) {
            Log.e("CampSiteDetail", "❌ Error updating adapters with saved selections", e);
        }
    }

    private void applyPendingSavedSelections() {
        if (!savedSelectionsDataLoaded) {
            Log.d("CampSiteDetail", "ℹ️ No pending saved selections to apply");
            return;
        }

        Log.d("CampSiteDetail", "🔄 Applying pending saved selections to adapters");

        try {
            // Apply camp type quantities
            if (campTypeAdapter != null && !pendingSavedCampTypes.isEmpty()) {
                Log.d("CampSiteDetail", "🔄 Applying saved camp type quantities");

                // Update internal maps
                selectedQuantity.clear();
                selectedQuantity.putAll(pendingSavedCampTypes);

                // Update adapter
                campTypeAdapter.applySavedQuantities(pendingSavedCampTypes);

                Log.d("CampSiteDetail", "✅ Camp type quantities applied: " + pendingSavedCampTypes.size());
            } else {
                Log.w("CampSiteDetail", "⚠️ Cannot apply camp type quantities - adapter: " +
                        (campTypeAdapter != null) + ", quantities: " + pendingSavedCampTypes.size());
            }

            // Apply selection quantities
            if (selectionAdapter != null && !pendingSavedSelections.isEmpty()) {
                Log.d("CampSiteDetail", "🔄 Applying saved selection quantities");

                // Update internal maps
                selectionSelectedQuantity.clear();
                selectionSelectedQuantity.putAll(pendingSavedSelections);

                // Update adapter
                selectionAdapter.applySavedQuantities(pendingSavedSelections);

                Log.d("CampSiteDetail", "✅ Selection quantities applied: " + pendingSavedSelections.size());
            } else {
                Log.w("CampSiteDetail", "⚠️ Cannot apply selection quantities - adapter: " +
                        (selectionAdapter != null) + ", quantities: " + pendingSavedSelections.size());
            }

            // Update totals and button state
            updateTotal();
            updateBtnState();

            // Clear pending data
            pendingSavedCampTypes.clear();
            pendingSavedSelections.clear();
            savedSelectionsDataLoaded = false;

            Log.d("CampSiteDetail", "✅ Successfully applied all pending saved selections");

        } catch (Exception e) {
            Log.e("CampSiteDetail", "❌ Error applying pending saved selections", e);
        }
    }

    public void setUpViewPager(){
        if (!campSiteResponse.getGalleries().isEmpty()) {
            List<String> urls = campSiteResponse.getGalleries().stream().map(GalleryResponse::getPath).collect(Collectors.toList());
            imageSliderAdapter = new ImageSliderAdapter(urls);

            viewPager2.setAdapter(imageSliderAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();

        // Reset flag so selections can be loaded again next time
        shouldLoadSavedSelections = true;
        savedSelectionsDataLoaded = false;
        pendingSavedCampTypes.clear();
        pendingSavedSelections.clear();
    }

    @Override
    public void onStop() {
        if (mapView != null) mapView.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}
