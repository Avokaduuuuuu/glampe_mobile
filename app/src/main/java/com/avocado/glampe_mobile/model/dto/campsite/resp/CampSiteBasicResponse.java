package com.avocado.glampe_mobile.model.dto.campsite.resp;

import com.avocado.glampe_mobile.model.dto.gallery.GalleryResponse;

import java.time.LocalDateTime;
import java.util.List;

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
public class CampSiteBasicResponse {
    Long id;
    String name;
    String address;
    String city;
    Double latitude;
    Double longitude;
    String status;
    String message;
    Double depositRate;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<GalleryResponse> galleries;
}
