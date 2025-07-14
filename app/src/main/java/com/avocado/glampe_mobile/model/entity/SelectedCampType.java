package com.avocado.glampe_mobile.model.entity;

import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.dto.facility.resp.FacilityResponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectedCampType {
    private Long campTypeId;
    private String type;
    private int capacity;
    private double pricePerNight;
    private double weekendRate;
    private int quantity;
    private String image;
    private List<FacilityResponse> facilities;

    public double getTotalPrice(int nights) {
        return pricePerNight * quantity * nights;
    }

    public SelectedCampType(CampTypeResponse campType) {
        this.campTypeId = campType.getId();
        this.type = campType.getType();
        this.capacity = campType.getCapacity();
        this.pricePerNight = campType.getPrice();
        this.weekendRate = campType.getWeekendPrice();
        this.quantity = campType.getSelectedQuantity();
        this.image = campType.getImage();
        this.facilities = campType.getFacilities();
    }


}
