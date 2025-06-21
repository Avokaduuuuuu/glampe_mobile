package com.avocado.glampe_mobile.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private Integer currentPage;
    private Integer pageSize;
    private String sortBy;
    private String sortOrder;
    private Long totalRecords;
    private Integer totalPages;
    private List<T> records;
}
