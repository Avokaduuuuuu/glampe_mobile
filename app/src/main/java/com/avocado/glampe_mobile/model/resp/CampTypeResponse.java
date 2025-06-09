package com.avocado.glampe_mobile.model.resp;

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
    Double weekendRate;
    String updatedAt;
    Integer quantity;
    Boolean status;
    Integer campSiteId;
    String image;
    List<FacilityResponse> facilities;
}
