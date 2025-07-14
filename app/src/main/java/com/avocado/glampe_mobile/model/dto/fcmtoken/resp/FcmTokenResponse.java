package com.avocado.glampe_mobile.model.dto.fcmtoken.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcmTokenResponse {
    Long id;
    String token;
    String deviceId;
    Long userId;
}
