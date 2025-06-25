package com.avocado.glampe_mobile.model.dto.campsite.resp;

import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;
import com.avocado.glampe_mobile.model.dto.gallery.GalleryResponse;
import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;
import com.avocado.glampe_mobile.model.dto.selection.resp.SelectionResponse;
import com.avocado.glampe_mobile.model.dto.user.resp.UserResponse;
import com.avocado.glampe_mobile.model.dto.utility.resp.UtilityResponse;

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
public class CampSiteResponse {
    Long id;
    String name;
    String address;
    String city;
    Double latitude;
    Double longitude;
    String createdTime;
    String status;
    String description;
    Double depositRate;
    UserResponse user;
    List<GalleryResponse> galleries;
    List<SelectionResponse> selections;
    List<PlaceTypeResponse> placeTypes;
    List<UtilityResponse> utilities;
    List<CampTypeResponse> campTypes;
}
