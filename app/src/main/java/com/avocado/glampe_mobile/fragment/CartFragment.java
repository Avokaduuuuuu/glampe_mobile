package com.avocado.glampe_mobile.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avocado.glampe_mobile.R;
import com.avocado.glampe_mobile.adapter.CartDisplayAdapter;
import com.avocado.glampe_mobile.di.CampSiteCartManager;
import com.avocado.glampe_mobile.model.entity.CampSiteCart;
import com.avocado.glampe_mobile.model.entity.CartItem;
import com.avocado.glampe_mobile.model.entity.PriceFormat;
import com.google.android.material.button.MaterialButton;

public class CartFragment extends Fragment {

    // Views
    private TextView tvCartItemCount, tvCartTotal, tvFinalTotal, tvSubtotal, tvAddonsTotal;
    private RecyclerView rvCartItems;
    private LinearLayout cartContentLayout, emptyCartLayout;
    private MaterialButton btnExploreCampsites;
    private ImageButton btnClearCart, btnBack;

    // Components
    private CartDisplayAdapter cartDisplayAdapter;
    private CampSiteCartManager cartManager;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("CartFragment", "🛒 onViewCreated - Setting up cart fragment");

        initViews(view);
        initCartManager();
        setupAdapter();
        setupClickListeners();

        // IMPORTANT: Observe BEFORE checking current state
        observeCartData();

        // Force initial load after setup
        forceCartRefresh();
    }

    private void initViews(View view) {
        // Text views
        tvCartItemCount = view.findViewById(R.id.tvCartItemCount);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        tvFinalTotal = view.findViewById(R.id.tvFinalTotal);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvAddonsTotal = view.findViewById(R.id.tvAddonsTotal); // Fix: Correct ID

        // RecyclerView
        rvCartItems = view.findViewById(R.id.rvCartItems);
        rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Layouts
        cartContentLayout = view.findViewById(R.id.cartContentLayout);
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);


        btnExploreCampsites = view.findViewById(R.id.btnExploreCampsites);
        btnClearCart = view.findViewById(R.id.btnClearCart);
        btnBack = view.findViewById(R.id.btnBack);
        navController = Navigation.findNavController(view);

        Log.d("CartFragment", "✅ Views initialized");
    }

    private void initCartManager() {
        cartManager = CampSiteCartManager.getInstance(requireContext());

        // Debug current cart state
        Log.d("CartFragment", "🛒 Cart manager initialized:");
        Log.d("CartFragment", "   - Item count: " + cartManager.getCartItemCount());
        Log.d("CartFragment", "   - Is empty: " + cartManager.isCartEmpty());
        Log.d("CartFragment", "   - Total amount: " + cartManager.getCartTotalAmount());

        // Debug cart items
        if (cartManager.getCart() != null && cartManager.getCart().getItems() != null) {
            Log.d("CartFragment", "   - Items in cart: " + cartManager.getCart().getItems().size());
            for (int i = 0; i < cartManager.getCart().getItems().size(); i++) {
                var item = cartManager.getCart().getItems().get(i);
                Log.d("CartFragment", "     * Item " + i + ": " + item.getCampsiteName());
            }
        }
    }

    private void setupAdapter() {
        cartDisplayAdapter = new CartDisplayAdapter(requireContext());

//        // Set remove item callback
//        cartDisplayAdapter.setOnRemoveItemListener(itemId -> {
//            Log.d("CartFragment", "🗑️ Removing item: " + itemId);
//            cartManager.removeItem(itemId);
//        });
        cartDisplayAdapter.setOnCartActionListener(new CartDisplayAdapter.OnCartActionListener() {
            @Override
            public void onRemoveItem(CartItem item) {

            }

            @Override
            public void onEditItem(CartItem item) {

            }

            @Override
            public void onViewCampsite(CartItem item) {
                Bundle bundle = new Bundle();
                bundle.putLong("campSiteId", item.getCampsiteId());
                navController.navigate(R.id.action_cartFragment_to_campSiteDetailFragment, bundle);
            }
        });
        rvCartItems.setAdapter(cartDisplayAdapter);

        Log.d("CartFragment", "✅ Adapter setup complete");
    }

    private void setupClickListeners() {
        // Clear cart button
        if (btnClearCart != null) {
            btnClearCart.setOnClickListener(v -> {
                Log.d("CartFragment", "🗑️ Clear cart button clicked");
                cartManager.clearCart();
            });
        }

        // Explore campsites button (for empty cart)
        if (btnExploreCampsites != null) {
            btnExploreCampsites.setOnClickListener(v -> {
                Log.d("CartFragment", "🔍 Explore button clicked");
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        btnBack.setOnClickListener(v -> navController.popBackStack());


        Log.d("CartFragment", "✅ Click listeners setup complete");
    }

    private void observeCartData() {
        Log.d("CartFragment", "👀 Setting up cart data observers");

        // Observe cart changes
        cartManager.getCartLiveData().observe(getViewLifecycleOwner(), new Observer<CampSiteCart>() {
            @Override
            public void onChanged(CampSiteCart cart) {
                Log.d("CartFragment", "🔄 Cart data changed:");
                if (cart != null) {
                    Log.d("CartFragment", "   - Items: " + cart.getTotalItems());
                    Log.d("CartFragment", "   - Empty: " + cart.isEmpty());
                    Log.d("CartFragment", "   - Total: " + cart.getTotalAmount());

                    updateCartDisplay(cart);
                } else {
                    Log.w("CartFragment", "⚠️ Received null cart data");
                    showEmptyCart();
                }
            }
        });

        Log.d("CartFragment", "✅ Observers setup complete");
    }

    private void forceCartRefresh() {
        Log.d("CartFragment", "🔄 Forcing cart refresh");

        // Get current cart state and trigger update
        CampSiteCart currentCart = cartManager.getCart();
        if (currentCart != null) {
            Log.d("CartFragment", "   - Current cart items: " + currentCart.getTotalItems());
            updateCartDisplay(currentCart);
        } else {
            Log.w("CartFragment", "   - Current cart is null");
            showEmptyCart();
        }
    }

    private void updateCartDisplay(CampSiteCart cart) {
        Log.d("CartFragment", "🎨 Updating cart display");

        if (cart == null || cart.isEmpty()) {
            Log.d("CartFragment", "   - Showing empty cart");
            showEmptyCart();
        } else {
            Log.d("CartFragment", "   - Showing cart content with " + cart.getTotalItems() + " items");
            showCartContent(cart);
        }
    }

    private void showEmptyCart() {
        Log.d("CartFragment", "📭 Showing empty cart layout");

        if (cartContentLayout != null) {
            cartContentLayout.setVisibility(View.GONE);
        }
        if (emptyCartLayout != null) {
            emptyCartLayout.setVisibility(View.VISIBLE);
            tvSubtotal.setText(PriceFormat.formatUsd(0.0));
            tvAddonsTotal.setText(PriceFormat.formatUsd(0.0));
            tvCartTotal.setText(PriceFormat.formatUsd(0.0));
        }
    }

    private void showCartContent(CampSiteCart cart) {
        Log.d("CartFragment", "📦 Showing cart content layout");

        if (emptyCartLayout != null) {
            emptyCartLayout.setVisibility(View.GONE);
        }
        if (cartContentLayout != null) {
            cartContentLayout.setVisibility(View.VISIBLE);
        }

        // Update adapter with current items
        if (cartDisplayAdapter != null && cart.getItems() != null) {
            Log.d("CartFragment", "   - Updating adapter with " + cart.getItems().size() + " items");
            cartDisplayAdapter.updateItems(cart.getItems());

            // Force adapter refresh
            cartDisplayAdapter.notifyDataSetChanged();
        }

        // Update cart summary
        updateCartSummary(cart);

    }

    private void updateCartSummary(CampSiteCart cart) {
        Log.d("CartFragment", "📊 Updating cart summary");

        try {
            // Update item count
            int itemCount = cart.getTotalItems();
            if (tvCartItemCount != null) {
                tvCartItemCount.setText(itemCount + (itemCount == 1 ? " Campsite" : " Campsites"));
            }

            // Calculate totals - with null checks
            double campTypesTotal = 0;
            double addonsTotal = 0;
            double finalTotal = 0;

            try {
                campTypesTotal = cart.getTotalCampTypesAmount();
                addonsTotal = cart.getTotalAddonsAmount();
                finalTotal = cart.getTotalAmount();
            } catch (Exception e) {
                Log.e("CartFragment", "❌ Error calculating totals", e);
            }

            // Update price displays with null checks
            if (tvSubtotal != null) {
                tvSubtotal.setText(PriceFormat.formatUsd(campTypesTotal));
            }
            if (tvAddonsTotal != null) {
                tvAddonsTotal.setText(PriceFormat.formatUsd(addonsTotal));
            }
            if (tvCartTotal != null) {
                tvCartTotal.setText(PriceFormat.formatUsd(finalTotal));
            }
            if (tvFinalTotal != null) {
                tvFinalTotal.setText(PriceFormat.formatUsd(finalTotal));
            }

            Log.d("CartFragment", "   - Summary updated: " + itemCount + " items, total: " + finalTotal);

        } catch (Exception e) {
            Log.e("CartFragment", "❌ Error updating cart summary", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CartFragment", "🔄 onResume - Refreshing cart");

        // Force refresh when fragment resumes
        forceCartRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CartFragment", "🗑️ onDestroyView");
    }
}