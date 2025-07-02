package com.avocado.glampe_mobile.model.dto.booking.resp;

import com.avocado.glampe_mobile.model.dto.bookingdetail.resp.BookingDetailResponse;
import com.avocado.glampe_mobile.model.dto.bookingselection.resp.BookingSelectionResponse;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteBasicResponse;
import com.avocado.glampe_mobile.model.dto.user.resp.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class BookingResponse {
    Long id;
    UserResponse user;
    String status;
    LocalDateTime checkInAt;
    LocalDateTime checkOutAt;
    BigDecimal totalAmount;
    BigDecimal systemFee;
    BigDecimal netAmount;
    String comment;
    Integer rating;
    String message;
    Boolean isPointUsed;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    CampSiteBasicResponse campSite;
    List<BookingDetailResponse> bookingDetails;
    List<BookingSelectionResponse> bookingSelections;
}
