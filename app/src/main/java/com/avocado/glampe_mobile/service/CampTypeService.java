package com.avocado.glampe_mobile.service;

import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.camptype.resp.CampTypeResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface CampTypeService {
    @GET("/api/v1/campsites/camp-types")
    Call<ApiResponse<List<CampTypeResponse>>> fetchAllCampTypes(
            @QueryMap Map<String, String> params
            );
}
