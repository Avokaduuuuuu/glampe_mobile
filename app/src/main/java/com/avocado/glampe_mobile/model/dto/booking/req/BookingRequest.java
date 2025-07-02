package com.avocado.glampe_mobile.model.dto.booking.req;

import com.avocado.glampe_mobile.model.dto.bookingdetail.req.BookingDetailRequest;
import com.avocado.glampe_mobile.model.dto.selection.req.BookingSelectionRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class BookingRequest {
    Long userId;
    Long campSiteId;
    BigDecimal totalAmount;
    LocalDateTime checkInTime;
    LocalDateTime checkOutTime;
    List<BookingDetailRequest> bookingDetailRequests;
    List<BookingSelectionRequest> bookingSelectionRequests;


}
