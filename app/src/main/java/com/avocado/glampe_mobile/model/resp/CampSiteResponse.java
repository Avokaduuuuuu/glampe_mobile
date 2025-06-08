package com.avocado.glampe_mobile.model.resp;

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
    Integer id;
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
    List<ImageResponse> imageList;
    List<SelectionResponse> campSiteSelectionList;
    List<PlaceTypeResponse> campSitePlaceTypeList;
    List<UtilityResponse> campSiteUtilityList;
    List<CampTypeResponse> campSiteCampTypeList;
}
