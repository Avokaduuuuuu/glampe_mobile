package com.avocado.glampe_mobile.model.dto.booking.filter;

import com.avocado.glampe_mobile.model.dto.PageRequest;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BookingFilterParams extends PageRequest {
    Long userId;


    public Map<String, String> toMap() {
        Map<String, String> map = super.toMap();
        if (userId != null) map.put("userId", this.userId.toString());

        return map;
    }
}
