package com.avocado.glampe_mobile.service;

import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.placetype.resp.PlaceTypeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PlaceTypeService {

    @GET("/api/v1/campsites/place-types")
    Call<ApiResponse<List<PlaceTypeResponse>>> fetchAllPlaceTypes();
}
