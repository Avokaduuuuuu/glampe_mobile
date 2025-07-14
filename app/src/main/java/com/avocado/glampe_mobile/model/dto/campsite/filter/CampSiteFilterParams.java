package com.avocado.glampe_mobile.model.dto.campsite.filter;


import com.avocado.glampe_mobile.model.dto.PageRequest;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CampSiteFilterParams extends PageRequest {
    private String name;
    private String address;
    private String city;
    private String status;
    private Long placeTypeId;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (this.getCurrentPage() != null) map.put("currentPage", this.getCurrentPage().toString());
        if (this.getPageSize() != null) map.put("pageSize", this.getPageSize().toString());
        if (this.getSortBy() != null) map.put("sortBy", this.getSortBy());
        if (this.getSortOrder() != null) map.put("sortOrder", this.getSortOrder());
        if (this.getStatus() != null) map.put("status", this.getStatus());
        if (this.getCity() != null) map.put("city", this.getCity());
        if (this.getName() != null) map.put("name", this.getName());
        if (this.placeTypeId != null) map.put("placeTypeId", this.getPlaceTypeId().toString());
        return map;
    }

}
