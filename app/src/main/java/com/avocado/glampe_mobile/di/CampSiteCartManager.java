package com.avocado.glampe_mobile.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.entity.CampSiteCart;
import com.avocado.glampe_mobile.model.entity.CartItem;
import com.avocado.glampe_mobile.model.entity.SelectedAddon;
import com.avocado.glampe_mobile.model.entity.SelectedCampType;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

public class CampSiteCartManager {
    private static CampSiteCartManager instance;
    /**
     * -- GETTER --
     *  Get cart for display
     */
    @Getter
    private CampSiteCart cart;
    private MutableLiveData<CampSiteCart> cartLiveData;
    private MutableLiveData<Integer> cartBadgeLiveData;
    private SharedPreferences prefs;
    private Gson gson;

    private static final String CART_PREFS_NAME = "campsite_cart_prefs";
    private static final String CART_DATA_KEY = "cart_data";

    private CampSiteCartManager(Context context) {
        cart = new CampSiteCart();
        cartLiveData = new MutableLiveData<>(cart);
        cartBadgeLiveData = new MutableLiveData<>(0);
        prefs = context.getSharedPreferences(CART_PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCartFromLocal();
    }

    public static CampSiteCartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CampSiteCartManager(context.getApplicationContext());
        }
        return instance;
    }

    // ==================== PUBLIC METHODS ====================

    /**
     * Save current campsite selection to cart
     */
    public void saveCurrentSelection(Long campsiteId, String campsiteName,
                                     String campsiteImage, String campsiteAddress,
                                     Long checkInDate, Long checkOutDate,
                                     List<CampTypeResponse> selectedCampTypes,
                                     List<SelectionResponse> selectedAddons) {

        // Create cart item
        CartItem cartItem = new CartItem(campsiteId, campsiteName, campsiteImage, campsiteAddress);

        // Set dates
        cartItem.setCheckInDate(checkInDate);
        cartItem.setCheckOutDate(checkOutDate);
        if (checkInDate != null && checkOutDate != null) {
            long diffInMillis = checkOutDate - checkInDate;
            cartItem.setNumberOfNights((int) (diffInMillis / (1000 * 60 * 60 * 24)));
        }

        // Add selected camp types
        for (CampTypeResponse campType : selectedCampTypes) {
            if (campType.getSelectedQuantity() > 0) {
                cartItem.getSelectedCampTypes().add(new SelectedCampType(campType));
            }
        }

        // Add selected addons
        for (SelectionResponse addon : selectedAddons) {
            if (addon.getSelectedQuantity() > 0) {
                cartItem.getSelectedAddons().add(new SelectedAddon(addon));
            }
        }

        // Only save if there are actual selections
        if (cartItem.hasSelections()) {
            cart.addOrUpdateItem(cartItem);
            saveCartToLocal();
            notifyCartUpdated();
        }
    }

    /**
     * Load saved selection for a campsite
     */
    public CartItem getSavedSelection(Long campsiteId) {
        return cart.findItemByCampsite(campsiteId);
    }

    /**
     * Remove item from cart
     */
    public void removeItem(String itemId) {
        cart.removeItem(itemId);
        saveCartToLocal();
        notifyCartUpdated();
    }

    /**
     * Clear entire cart
     */
    public void clearCart() {
        cart.clearCart();
        saveCartToLocal();
        notifyCartUpdated();
    }

    /**
     * Get cart LiveData for observing
     */
    public LiveData<CampSiteCart> getCartLiveData() {
        return cartLiveData;
    }

    /**
     * Get cart badge count LiveData
     */
    public LiveData<Integer> getCartBadgeLiveData() {
        return cartBadgeLiveData;
    }

    /**
     * Get cart item count
     */
    public int getCartItemCount() {
        return cart.getTotalItems();
    }

    /**
     * Get cart total amount
     */
    public double getCartTotalAmount() {
        return cart.getTotalAmount();
    }

    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    /**
     * Apply saved selection to UI (for when user returns to campsite)
     */
    public void applySavedSelectionToUI(Long campsiteId,
                                        List<CampTypeResponse> campTypes,
                                        List<SelectionResponse> addons) {
        CartItem savedItem = getSavedSelection(campsiteId);
        if (savedItem == null) return;

        // Reset all quantities first
        for (CampTypeResponse campType : campTypes) {
            campType.setSelectedQuantity(0);
        }
        for (SelectionResponse addon : addons) {
            addon.setSelectedQuantity(0);
        }

        // Apply saved camp type quantities
        for (SelectedCampType saved : savedItem.getSelectedCampTypes()) {
            for (CampTypeResponse campType : campTypes) {
                if (campType.getId().equals(saved.getCampTypeId())) {
                    campType.setSelectedQuantity(saved.getQuantity());
                    break;
                }
            }
        }

        // Apply saved addon quantities
        for (SelectedAddon saved : savedItem.getSelectedAddons()) {
            for (SelectionResponse addon : addons) {
                if (addon.getId().equals(saved.getSelectionId())) {
                    addon.setSelectedQuantity(saved.getQuantity());
                    break;
                }
            }
        }
    }

    /**
     * Get saved dates for a campsite
     */
    public Pair<Long, Long> getSavedDates(Long campsiteId) {
        CartItem savedItem = getSavedSelection(campsiteId);
        if (savedItem != null) {
            return new Pair<>(savedItem.getCheckInDate(), savedItem.getCheckOutDate());
        }
        return null;
    }

    // ==================== PRIVATE METHODS ====================

    private void saveCartToLocal() {
        try {
            String cartJson = gson.toJson(cart);
            prefs.edit().putString(CART_DATA_KEY, cartJson).apply();
        } catch (Exception e) {
            Log.e("CartManager", "Error saving cart to local", e);
        }
    }

    private void loadCartFromLocal() {
        try {
            String cartJson = prefs.getString(CART_DATA_KEY, "");
            if (!cartJson.isEmpty()) {
                CampSiteCart loadedCart = gson.fromJson(cartJson, CampSiteCart.class);
                if (loadedCart != null) {
                    cart = loadedCart;

                }
            }
        } catch (Exception e) {
            Log.e("CartManager", "Error loading cart from local", e);
            cart = new CampSiteCart(); // fallback to empty cart
        }
    }

    private void removeExpiredItems() {
        long currentTime = System.currentTimeMillis();
        long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L;

        cart.getItems().removeIf(item ->
                (currentTime - item.getCreatedAt()) > sevenDaysInMillis);
    }

    private void notifyCartUpdated() {
        cartLiveData.postValue(cart);
        cartBadgeLiveData.postValue(cart.getTotalItems());
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Format price for display
     */
    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price);
    }

    /**
     * Format date for display
     */
    public static String formatDate(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Calculate nights between dates
     */
    public static int calculateNights(Long checkIn, Long checkOut) {
        if (checkIn == null || checkOut == null) return 0;
        long diffInMillis = checkOut - checkIn;
        return (int) (diffInMillis / (1000 * 60 * 60 * 24));
    }
}
