package com.avocado.glampe_mobile.model.dto.fcmtoken.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmTokenUpdateRequest {
    Long userId;
    String token;
    String deviceId;
}
