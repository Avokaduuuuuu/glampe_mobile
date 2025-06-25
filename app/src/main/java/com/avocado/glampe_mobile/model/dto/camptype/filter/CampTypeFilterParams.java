package com.avocado.glampe_mobile.model.dto.camptype.filter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampTypeFilterParams {
    private Long campSiteId;
    private LocalDate checkInAt;
    private LocalDate checkOutAt;

    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (campSiteId != null) map.put("campSiteId", campSiteId.toString());
        if (checkInAt != null) map.put("checkInAt", formatter.format(checkInAt));
        if (checkOutAt != null) map.put("checkOutAt", formatter.format(checkOutAt));
        return map;
    }
}
