package com.avocado.glampe_mobile.model.dto;


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
}
