package com.avocado.glampe_mobile.model.entity;

import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectedAddon {
    private Long selectionId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String image;

    public double getTotalPrice() {
        return price * quantity;
    }

    public SelectedAddon(SelectionResponse selection) {
        this.selectionId = selection.getId();
        this.name = selection.getName();
        this.description = selection.getDescription();
        this.price = selection.getPrice().doubleValue();
        this.quantity = selection.getSelectedQuantity();
        this.image = selection.getImage();
    }
}
