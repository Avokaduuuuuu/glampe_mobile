package com.avocado.glampe_mobile.model.dto.bookingdetail.resp;


import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class BookingDetailResponse {
    Long id;
    LocalDateTime checkInAt;
    LocalDateTime checkOutAt;
    BigDecimal amount;
    BigDecimal add;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long bookingId;
    CampTypeResponse campType;
}
