package com.avocado.glampe_mobile.model.dto.payment.resp;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentResponse {
    Long id;
    Long bookingId;
    String paymentMethod;
    String paymentIntentId;
    BigDecimal totalAmount;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime completedAt;
}
