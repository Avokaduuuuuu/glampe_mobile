package com.avocado.glampe_mobile.model.dto.selection.resp;

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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SelectionResponse {
    Integer id;
    String name;
    String description;
    BigDecimal price;
    String image;
    Boolean isDeleted;
    @Builder.Default
    Integer selectedQuantity = 0;
}
