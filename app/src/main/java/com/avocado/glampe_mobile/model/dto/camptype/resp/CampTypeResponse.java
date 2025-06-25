package com.avocado.glampe_mobile.model.dto.camptype.resp;

import com.avocado.glampe_mobile.model.dto.facility.resp.FacilityResponse;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampTypeResponse {
    Integer id;
    String type;
    Integer capacity;
    Double price;
    Double weekendPrice;
    String updatedAt;
    Integer quantity;
    Long remainQuantity;
    Boolean isDeleted;
    Integer campSiteId;
    String image;
    List<FacilityResponse> facilities;
    @Builder.Default
    Integer selectedQuantity = 0;
}
