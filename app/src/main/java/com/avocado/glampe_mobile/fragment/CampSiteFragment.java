package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.avocado.glampe_mobile.adapter.PlaceTypeAdapter;
import com.avocado.glampe_mobile.di.CampSiteCartManager;
import com.avocado.glampe_mobile.model.dto.campsite.filter.CampSiteFilterParams;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.dto.facility.resp.FacilityResponse;
import com.avocado.glampe_mobile.model.dto.gallery.GalleryResponse;
import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;
import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.dto.user.resp.UserResponse;
import com.avocado.glampe_mobile.model.dto.utility.resp.UtilityResponse;
import com.avocado.glampe_mobile.model.entity.CampSiteCart;
import com.avocado.glampe_mobile.viewModel.BottomNavViewModel;
import com.avocado.glampe_mobile.viewModel.CampSiteViewModel;
import com.avocado.glampe_mobile.viewModel.PlaceTypeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

public class CampSiteFragment extends Fragment {

    private LottieAnimationView loadingView;
    private RecyclerView recyclerView, rvPlaceTypes;
    private BottomNavViewModel bottomNavViewModel;
    private NavController navController;
    private CampSiteViewModel campSiteViewModel;
    private PlaceTypeViewModel placeTypeViewModel;
    private List<CampSiteResponse> campSiteResponses = new ArrayList<>();
    private NestedScrollView nestedScrollView;
    private boolean isLoadingMore = false;
    private boolean hasMorePages = true;
    private CampSiteAdapter campSiteAdapter;
    private PlaceTypeAdapter placeTypeAdapter;

    // Instance variables để track loading states
    private boolean isCampSitesLoading = true;
    private boolean isPlaceTypesLoading = true;
    private boolean campSitesDataReceived = false;
    private boolean placeTypesDataReceived = false;

    // Cart UI components
    private CampSiteCartManager cartManager;
    private FrameLayout cartButtonContainer;
    private TextView cartBadge;
    private ImageView cartIcon;
    private LinearLayout searchBarContainer;

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
        initCartManager();
        initCartButton(view);
        setupRecyclerView();
        observeViewModel();
        observeCartChanges();

        if (campSiteAdapter == null || campSiteAdapter.getItemCount() == 0) {
            loadInitData();
        }
        setupNestedScrollListener(nestedScrollView);
        Log.d("CampSiteFragment", cartManager.getCart().toString());
    }

    private void initCartManager() {
        cartManager = CampSiteCartManager.getInstance(requireContext());
        Log.d("CampSiteFragment", "🛒 Cart manager initialized");
        Log.d("CampSiteFragment", cartManager.getCart().toString());
    }

    private void initCartButton(View view) {
        // Find cart UI components
        searchBarContainer = view.findViewById(R.id.searchBarContainer);
        cartButtonContainer = view.findViewById(R.id.cartButtonContainer);
        cartBadge = view.findViewById(R.id.cartBadge);
        cartIcon = view.findViewById(R.id.cartIcon);

        if (cartButtonContainer != null) {
            // Set click listener for cart button
            cartButtonContainer.setOnClickListener(v -> {
                Log.d("CampSiteFragment", "🛒 Cart button clicked");
                handleCartButtonClick();
            });

            Log.d("CampSiteFragment", "🛒 Cart button initialized");
        } else {
            Log.e("CampSiteFragment", "❌ Cart button container not found in layout");
        }

        // Set click listener for search bar (if needed)
        if (searchBarContainer != null) {
            searchBarContainer.setOnClickListener(v -> {
                // Navigate to search fragment or show search dialog
                Log.d("CampSiteFragment", "🔍 Search bar clicked");
                // TODO: Implement search functionality
            });
        }
    }

    private void initView(View view) {
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerView);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        loadingView = view.findViewById(R.id.loadingAnimation);
        rvPlaceTypes = view.findViewById(R.id.rvPlaceTypes);

        // Initially show loading
        showLoading();
    }

    private void initViewModels() {
        bottomNavViewModel = new ViewModelProvider(requireActivity()).get(BottomNavViewModel.class);
        campSiteViewModel = new ViewModelProvider(this).get(CampSiteViewModel.class);
        placeTypeViewModel = new ViewModelProvider(this).get(PlaceTypeViewModel.class);
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

    // ==================== CART FUNCTIONALITY ====================

    private void observeCartChanges() {
        // Observe cart badge count changes
        cartManager.getCartBadgeLiveData().observe(getViewLifecycleOwner(), itemCount -> {
            Log.d("CampSiteFragment", "🛒 Cart items count: " + itemCount);
            updateCartBadge(itemCount);
        });

        // Observe cart data changes
        cartManager.getCartLiveData().observe(getViewLifecycleOwner(), cart -> {
            if (cart != null) {
                Log.d("CampSiteFragment", "🛒 Cart updated - Total items: " + cart.getTotalItems() +
                        ", Total amount: " + cart.getTotalAmount());

                // You can add additional cart-related UI updates here
                updateCartUI(cart);
            }
        });

        Log.d("CampSiteFragment", "🛒 Cart observers set up");
    }

    private void updateCartBadge(int itemCount) {
        if (cartBadge == null || cartIcon == null) {
            Log.w("CampSiteFragment", "⚠️ Cart UI components not found");
            return;
        }

        if (itemCount > 0) {
            // Show badge with count
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(itemCount > 99 ? "99+" : String.valueOf(itemCount));

            // Change to filled cart icon
            cartIcon.setImageResource(R.drawable.ic_shopping_cart_filled);

            // Add bounce animation
            animateCartBadge();

            Log.d("CampSiteFragment", "🛒 Cart badge updated: " + itemCount + " items");
        } else {
            // Hide badge
            cartBadge.setVisibility(View.GONE);
            cartIcon.setImageResource(R.drawable.ic_shopping_cart);

            Log.d("CampSiteFragment", "🛒 Cart badge hidden - no items");
        }
    }

    private void animateCartBadge() {
        if (cartBadge == null) return;

        cartBadge.animate()
                .scaleX(1.3f).scaleY(1.3f)
                .setDuration(150)
                .withEndAction(() -> {
                    cartBadge.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(150);
                });
    }

    private void updateCartUI(CampSiteCart cart) {
        // Additional cart-related UI updates can be added here
        // For example: showing cart summary, updating other UI elements, etc.

        if (cart.isEmpty()) {
            Log.d("CampSiteFragment", "🛒 Cart is empty");
        } else {
            Log.d("CampSiteFragment", "🛒 Cart contains " + cart.getTotalItems() +
                    " items worth " + CampSiteCartManager.formatPrice(cart.getTotalAmount()));
        }
    }

    private void handleCartButtonClick() {
        int itemCount = cartManager.getCartItemCount();

        if (itemCount == 0) {
            // Show message when cart is empty
            Toast.makeText(requireContext(), "Giỏ hàng trống. Hãy chọn địa điểm cắm trại!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to cart screen
        navigateToCart();
    }

    private void navigateToCart() {
        try {
            // Option 1: Navigate to CartFragment (recommended)
            navController.navigate(R.id.action_campSiteFragment_to_cartFragment);
            Log.d("CampSiteFragment", "🛒 Navigating to cart fragment");

        } catch (Exception e) {
            Log.e("CampSiteFragment", "❌ Failed to navigate to cart", e);

        }
    }


    // ==================== EXISTING METHODS ====================

    private void observeViewModel() {
        // Observe CampSite loading state
        campSiteViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                isCampSitesLoading = isLoading;
                Log.d("CampSiteFragment", "🏕️ CampSites loading: " + isLoading);
                checkAndUpdateLoadingState();
            }
        });

        // Observe PlaceType loading state
        placeTypeViewModel.getFetchAllLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                isPlaceTypesLoading = isLoading;
                Log.d("CampSiteFragment", "📍 PlaceTypes loading: " + isLoading);
                checkAndUpdateLoadingState();
            }
        });

        // Observe CampSite data
        campSiteViewModel.getCampSiteList().observe(getViewLifecycleOwner(), campSites -> {
            if (campSites != null) {
                Log.d("CampSiteFragment", "📋 Received " + campSites.size() + " camp sites");
                campSitesDataReceived = true;
                updateAdapter(campSites);
                checkAndUpdateLoadingState();
            }
        });

        // Observe PlaceType data
        placeTypeViewModel.getPlaceTypes().observe(getViewLifecycleOwner(), placeTypes -> {
            if (placeTypes != null) {
                Log.d("CampSiteFragment", "📋 Received " + placeTypes.size() + " place types");
                placeTypesDataReceived = true;
                updatePlaceTypeAdapter(placeTypes);
                checkAndUpdateLoadingState();
            }
        });
    }

    private void checkAndUpdateLoadingState() {
        boolean isAnyLoading = isCampSitesLoading || isPlaceTypesLoading;
        boolean isAllDataReceived = campSitesDataReceived && placeTypesDataReceived;

        Log.d("CampSiteFragment", "🔄 Loading state check:");
        Log.d("CampSiteFragment", "   - CampSites loading: " + isCampSitesLoading + ", data received: " + campSitesDataReceived);
        Log.d("CampSiteFragment", "   - PlaceTypes loading: " + isPlaceTypesLoading + ", data received: " + placeTypesDataReceived);
        Log.d("CampSiteFragment", "   - Any loading: " + isAnyLoading + ", All data received: " + isAllDataReceived);

        if (isAllDataReceived) {
            Log.d("CampSiteFragment", "✅ All data loaded - showing content");
            showContent();
        } else {
            Log.d("CampSiteFragment", "⏳ Still loading or waiting for data");
            // Only show loading if we haven't shown content yet
            if (nestedScrollView.getVisibility() != View.VISIBLE) {
                showLoading();
            }
        }
    }

    private void showLoading() {
        Log.d("CampSiteFragment", "🔄 Showing loading animation");
        loadingView.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
    }

    private void showContent() {
        Log.d("CampSiteFragment", "✅ Showing content");
        loadingView.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void loadInitData() {
        Log.d("CampSiteFragment", "🚀 Loading initial data");

        CampSiteFilterParams params = CampSiteFilterParams.builder()
                .currentPage(0)
                .pageSize(10)
                .sortBy("id")
                .sortOrder("ASC")
                .status("Available")
                .placeTypeId(1L)
                .build();

        campSiteViewModel.loadAllCampSites(params);
        placeTypeViewModel.fetchAll();
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

            private void handleBottomVisibility(int scrollY) {
                int deltaY = scrollY - lastScrollY;

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

            private void handlePagination(NestedScrollView v, int scrollY) {
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

        nestedScrollView.post(() -> {
            Log.d("ScrollDebug", "NestedScrollView height: " + nestedScrollView.getHeight());
            Log.d("ScrollDebug", "NestedScrollView child height: " + nestedScrollView.getChildAt(0).getHeight());
            Log.d("ScrollDebug", "Can scroll down: " + nestedScrollView.canScrollVertically(1));
        });
    }

    private void updateAdapter(List<CampSiteResponse> campSites) {
        campSiteAdapter.updateCampSites(campSites);
        Log.d("CampSiteFragment", "✅ Adapter updated with " + campSites.size() + " items");
    }

    private void updatePlaceTypeAdapter(List<PlaceTypeResponse> placeTypes) {
        rvPlaceTypes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        placeTypeAdapter = new PlaceTypeAdapter(placeTypes, placeType -> {
            CampSiteFilterParams params = CampSiteFilterParams.builder()
                    .currentPage(0)
                    .pageSize(10)
                    .sortBy("id")
                    .sortOrder("ASC")
                    .status("Available")
                    .placeTypeId(placeType.getId())
                    .build();
            campSiteViewModel.loadAllCampSites(params);
        });
        rvPlaceTypes.setAdapter(placeTypeAdapter);
        Log.d("CampSiteFragment", "✅ PlaceType adapter updated with " + placeTypes.size() + " items");
    }

    // ==================== LIFECYCLE METHODS ====================

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart data when fragment resumes
        Log.d("CampSiteFragment", "🔄 Fragment resumed - refreshing cart data");

        // Force update cart badge in case data changed in other fragments
        int currentCartCount = cartManager.getCartItemCount();
        updateCartBadge(currentCartCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CampSiteFragment", "🗑️ Fragment view destroyed");
    }
}