package com.avocado.glampe_mobile.model.dto;


import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PageRequest {
    private Integer currentPage;
    private Integer pageSize;
    private String sortBy;
    private String sortOrder;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (this.getCurrentPage() != null) map.put("currentPage", this.getCurrentPage().toString());
        if (this.getPageSize() != null) map.put("pageSize", this.getPageSize().toString());
        if (this.getSortBy() != null) map.put("sortBy", this.getSortBy());
        if (this.getSortOrder() != null) map.put("sortOrder", this.getSortOrder());
        return map;
    }
}
