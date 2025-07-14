package com.avocado.glampe_mobile.model.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private String id;  // unique id cho cart item
    private Long campsiteId;
    private String campsiteName;
    private String campsiteImage;
    private String campsiteAddress;

    // Date selection
    private Long checkInDate;
    private Long checkOutDate;
    private int numberOfNights;

    // Selected camp types
    private List<SelectedCampType> selectedCampTypes = new ArrayList<>();

    // Selected add-ons
    private List<SelectedAddon> selectedAddons = new ArrayList<>();

    // Pricing
    private double campTypeTotal;
    private double addonTotal;
    private double totalAmount;

    private long createdAt;

    public CartItem(Long campsiteId, String campsiteName, String campsiteImage, String campsiteAddress) {
        this.id = generateUniqueId();
        this.campsiteId = campsiteId;
        this.campsiteName = campsiteName;
        this.campsiteImage = campsiteImage;
        this.campsiteAddress = campsiteAddress;
    }

    private String generateUniqueId() {
        return "cart_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    public void calculateTotals() {
        // Calculate camp type total
        campTypeTotal = selectedCampTypes.stream()
                .mapToDouble(ct -> ct.getPricePerNight() * ct.getQuantity() * numberOfNights)
                .sum();

        // Calculate addon total
        addonTotal = selectedAddons.stream()
                .mapToDouble(addon -> addon.getPrice() * addon.getQuantity())
                .sum();

        totalAmount = campTypeTotal + addonTotal;
    }

    public boolean hasSelections() {
        return !selectedCampTypes.isEmpty() || !selectedAddons.isEmpty();
    }

    public boolean isValidForBooking() {
        return checkInDate != null && checkOutDate != null &&
                checkInDate < checkOutDate && hasSelections();
    }
}
