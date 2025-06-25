package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CampSiteAdapter;
import com.avocado.glampe_mobile.model.dto.campsite.filter.CampSiteFilterParams;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.dto.facility.resp.FacilityResponse;
import com.avocado.glampe_mobile.model.dto.gallery.GalleryResponse;
import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;
import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.dto.user.resp.UserResponse;
import com.avocado.glampe_mobile.model.dto.utility.resp.UtilityResponse;
import com.avocado.glampe_mobile.viewModel.BottomNavViewModel;
import com.avocado.glampe_mobile.viewModel.CampSiteViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

public class CampSiteFragment extends Fragment {

    private LottieAnimationView loadingView;

    private RecyclerView recyclerView;
    private BottomNavViewModel bottomNavViewModel;
    private NavController navController;

    private CampSiteViewModel campSiteViewModel;

    private List<CampSiteResponse> campSiteResponses = new ArrayList<>();
    private NestedScrollView nestedScrollView;

    private boolean isLoadingMore = false;
    private boolean hasMorePages = true;
    CampSiteAdapter campSiteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camp_site, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        initView(view);
        setupRecyclerView();
        observeViewModel();
        if (campSiteAdapter == null || campSiteAdapter.getItemCount() == 0) {
            loadInitData();
        }

        setupNestedScrollListener(nestedScrollView);
    }

    private void initView(View view) {
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerView);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        loadingView = view.findViewById(R.id.loadingAnimation);

        loadingView.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
    }

    private void initViewModels() {
        bottomNavViewModel = new ViewModelProvider(requireActivity()).get(BottomNavViewModel.class);
        campSiteViewModel = new ViewModelProvider(this).get(CampSiteViewModel.class);
    }

    private void setupRecyclerView() {
        campSiteAdapter = new CampSiteAdapter(new ArrayList<>(), campsite -> {
            Bundle bundle = new Bundle();
            bundle.putLong("campSiteId", campsite.getId());
            navController.navigate(R.id.action_campSiteFragment_to_campSiteDetailFragment, bundle);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(campSiteAdapter);
    }
    private void observeViewModel() {

        campSiteViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    Log.d("CampSiteFragment", "📍 SHOULD SHOW loading bar");
                    showLoading();
                } else {
                    Log.d("CampSiteFragment", "📍 SHOULD HIDE loading bar");
                    hideLoading();
                }
            } else {
                Log.w("CampSiteFragment", "⚠️ isLoading is NULL");
            }
        });

        campSiteViewModel.getCampSiteList().observe(getViewLifecycleOwner(), campSites -> {
            if (campSites != null) {
                Log.d("CampSiteFragment", "📋 Received " + campSites.size() + " items");
                updateAdapter(campSites); // ← USE PROCESSED LIST DIRECTLY
                loadingView.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void loadInitData() {
        CampSiteFilterParams params = CampSiteFilterParams.builder()
                .currentPage(0)
                .pageSize(10)
                .sortBy("id")
                .sortOrder("ASC")
                .status("Available")
                .build();
        campSiteViewModel.loadAllCampSites(params);
    }

    private void setupNestedScrollListener(NestedScrollView nestedScrollView) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private static final int SCROLL_THRESHOLD = 20;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                handleBottomVisibility(scrollY);
                handlePagination(v, scrollY);
            }
            private void handleBottomVisibility(int scrollY){
                int deltaY = scrollY - lastScrollY;

                // Only respond to significant scroll changes
                if (Math.abs(deltaY) > SCROLL_THRESHOLD) {
                    if (deltaY > 0) {
                        Log.d("ScrollDebug", "Scrolling DOWN - hiding nav");
                        bottomNavViewModel.hideBottomNav();
                    } else {
                        Log.d("ScrollDebug", "Scrolling UP - showing nav");
                        bottomNavViewModel.showBottomNav();
                    }
                    lastScrollY = scrollY;
                }
            }

            private void handlePagination(NestedScrollView v, int scrollY){
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (hasMorePages && !isLoadingMore) {
                        Log.d("CampSiteFragment", "🔄 Reached bottom - loading more data");
                        campSiteViewModel.loadMoreCampSites();
                    } else {
                        Log.d("CampSiteFragment", "⚠️ Not loading more - hasMorePages: " + hasMorePages + ", isLoadingMore: " + isLoadingMore);
                    }
                }
            }
        });

        // Debug info
        nestedScrollView.post(() -> {
            Log.d("ScrollDebug", "NestedScrollView height: " + nestedScrollView.getHeight());
            Log.d("ScrollDebug", "NestedScrollView child height: " + nestedScrollView.getChildAt(0).getHeight());
            Log.d("ScrollDebug", "Can scroll down: " + nestedScrollView.canScrollVertically(1));
        });
    }

    private void updateAdapter(List<CampSiteResponse> campSites) {
        // Update the existing adapter instead of creating new one
        campSiteAdapter.updateCampSites(campSites);

        Log.d("CampSiteFragment", "✅ Adapter updated with " + campSites.size() + " items");
    }
}
