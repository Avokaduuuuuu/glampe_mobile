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
public class CampSiteCart {
    private List<CartItem> items = new ArrayList<>();
    private double totalAmount;
    private int totalItems;

    public void addOrUpdateItem(CartItem item) {
        // Check if campsite already exists in cart
        CartItem existing = findItemByCampsite(item.getCampsiteId());
        if (existing != null) {
            // Update existing item
            items.remove(existing);
        }

        item.calculateTotals();
        items.add(item);
        calculateCartTotals();
    }

    public void removeItem(String itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        calculateCartTotals();
    }

    public CartItem findItemByCampsite(Long campsiteId) {
        return items.stream()
                .filter(item -> item.getCampsiteId().equals(campsiteId))
                .findFirst()
                .orElse(null);
    }

    public void clearCart() {
        items.clear();
        calculateCartTotals();
    }

    private void calculateCartTotals() {
        totalAmount = items.stream().mapToDouble(CartItem::getTotalAmount).sum();
        totalItems = items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getTotalCampTypesAmount() {
        return items.stream()
                .mapToDouble(CartItem::getCampTypeTotal)
                .sum();
    }


    public double getTotalAddonsAmount() {
        return items.stream()
                .mapToDouble(CartItem::getAddonTotal)
                .sum();
    }
}
