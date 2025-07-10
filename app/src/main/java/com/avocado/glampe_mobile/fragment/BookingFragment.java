package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.BookingAdapter;
import com.avocado.glampe_mobile.di.AuthManager;
import com.avocado.glampe_mobile.model.dto.booking.filter.BookingFilterParams;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;
import com.avocado.glampe_mobile.viewModel.BookingViewModel;
import com.avocado.glampe_mobile.viewModel.BottomNavViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {

    RecyclerView rvBookings;
    BookingAdapter bookingAdapter;

    BookingViewModel bookingViewModel;

    LottieAnimationView loadingAnimation;
    NestedScrollView nestedScrollView;
    BottomNavViewModel bottomNavViewModel;
    CardView cardView;
    NavController navController;

    MaterialButton btnActive, btnCompleted, btnCanceled;
    List<MaterialButton> btns;

    private List<BookingResponse> bookings = new ArrayList<>();

    private AuthUserResponse user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }
    private void initView(View view){
        rvBookings = view.findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        user = AuthManager.getAuthResponse(requireContext());
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        cardView = view.findViewById(R.id.cardBooking);
        navController = Navigation.findNavController(view);
        btnActive = view.findViewById(R.id.btnActive);
        btnCompleted = view.findViewById(R.id.btnCompleted);
        btnCanceled = view.findViewById(R.id.btnCancled);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initViewModel();
        observeFetchBookings();
        setUpScrollView();
        setUpButtons();

        btnActive.performClick();
    }

    private void initViewModel(){
        // Thử dùng Fragment scope thay vì Activity scope
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        bottomNavViewModel = new ViewModelProvider(requireActivity()).get(BottomNavViewModel.class);

        Log.d("Fragment", "ViewModel created: " + bookingViewModel.toString());
        Log.d("Fragment", "LiveData instance: " + bookingViewModel.getBookings().toString());
    }

    private void setUpScrollView() {
        rvBookings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int scrollDist = 0;
            private boolean isVisible = true;
            private static final int SCROLL_THRESHOLD = 20;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (Math.abs(scrollDist) > SCROLL_THRESHOLD) {
                    if (scrollDist > 0 && isVisible) {
                        bottomNavViewModel.hideBottomNav();
                        isVisible = false;
                    } else if (scrollDist < 0 && !isVisible) {
                        bottomNavViewModel.showBottomNav();
                        isVisible = true;
                    }
                    scrollDist = 0;
                }

                if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                    scrollDist += dy;
                }
            }
        });
    }



    private void observeFetchBookings(){
        Log.d("Fragment", "Setting up observers...");

        // Test basic observer first
        bookingViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("Loading Observer", "Loading state: " + isLoading);
            if (isLoading != null) {
                if (isLoading) {
                    Log.d("Loading Booking", " Loading");
                    loadingAnimation.setVisibility(View.VISIBLE);
                    rvBookings.setVisibility(View.GONE);
                } else {
                    loadingAnimation.setVisibility(View.GONE);
                    rvBookings.setVisibility(View.VISIBLE);
                }
            }
        });

        bookingViewModel.getBookings().observe(getViewLifecycleOwner(), pageResponse -> {
            Log.d("Observer", "===== OBSERVER TRIGGERED =====");
            Log.d("Observer", "Data is null: " + (pageResponse == null));

            if (pageResponse != null) {
                Log.d("Bookings Response", "Records count: " + pageResponse.getRecords().size());
                loadingAnimation.setVisibility(View.GONE);
                rvBookings.setVisibility(View.VISIBLE);

                if (bookingAdapter == null) {
                    Log.d("Adapter is null", "Create new Adapter");
                    bookingAdapter = new BookingAdapter(pageResponse.getRecords(), bookingResponse -> {
                        Bundle bundle = new Bundle();
                        bundle.putLong("bookingId", bookingResponse.getId());
                        navController.navigate(R.id.action_bookingFragment_to_bookingDetailFragment, bundle);
                    });
                    rvBookings.setAdapter(bookingAdapter);
                } else {
                    Log.d("Already have Adapter", "Update the current one");
                    bookingAdapter.updateData(pageResponse.getRecords());
                }
            } else {
                Log.d("Booking null", "Null data received");
            }
        });

        Log.d("Fragment", "Observers setup completed");
    }

    private void fetchBookings(BookingFilterParams params){
        Log.d("Fragment", "About to call loadBookings...");

        bookingViewModel.loadBookings(params);

        Log.d("Fragment", "loadBookings called");
    }

    private void setUpButtons() {
        btns = List.of(btnActive, btnCompleted, btnCanceled);

        btns.forEach(btn -> {
            btn.setOnClickListener(v -> {

                for (MaterialButton button : btns) {
                    button.setSelected(false);
                }
                if (btn.getId() == R.id.btnActive) {
                    fetchBookings(BookingFilterParams.builder()
                            .currentPage(0)
                            .pageSize(10)
                            .sortBy("id")
                            .sortOrder("DESC")
                            .userId(user.getUser().getId())
                            .statusList(List.of("Pending", "Deposit", "Accepted", "Check_In"))
                            .build());
                }

                if (btn.getId() == R.id.btnCompleted) {
                    fetchBookings(BookingFilterParams.builder()
                            .currentPage(0)
                            .pageSize(10)
                            .sortBy("id")
                            .sortOrder("DESC")
                            .userId(user.getUser().getId())
                            .statusList(List.of("Completed"))
                            .build());
                }

                if (btn.getId() == R.id.btnCancled) {
                    fetchBookings(BookingFilterParams.builder()
                            .currentPage(0)
                            .pageSize(10)
                            .sortBy("id")
                            .sortOrder("DESC")
                            .userId(user.getUser().getId())
                            .statusList(List.of("Denied", "Refund"))
                            .build());
                }
                btn.setSelected(true);
            });
        });
    }
}
