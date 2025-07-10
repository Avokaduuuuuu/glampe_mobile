package com.avocado.glampe_mobile.model.dto.bookingdetail.resp;

import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;

import java.math.BigDecimal;

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
public class CombinedBookingDetailResponse {
    CampTypeResponse campTypeResponse;
    @Builder.Default
    Integer quantity = 0;
    BigDecimal totalAmount;
}
