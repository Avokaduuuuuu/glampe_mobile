package com.avocado.glampe_mobile.model.dto.payment.req;


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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentIntentRequest {
    BigDecimal amount;
    String currency;
    Long bookingId;
    String method;
}
