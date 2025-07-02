package com.avocado.glampe_mobile.model.dto.bookingselection.resp;

import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;

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
public class BookingSelectionResponse {
    Long id;
    SelectionResponse selection;
    String name;
    Integer quantity;
}
