package com.avocado.glampe_mobile.service;

import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.campsite.filter.CampSiteFilterParams;
import com.avocado.glampe_mobile.model.dto.campsite.resp.CampSiteResponse;
import com.avocado.glampe_mobile.model.dto.PageResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface CampSiteService {

    @GET("/api/v1/campsites")
    Call<ApiResponse<PageResponse<CampSiteResponse>>> fetchAllCampSite(
            @QueryMap Map<String, String> params
            );

    @GET("/api/v1/campsites/{id}")
    Call<ApiResponse<CampSiteResponse>> fetchById(
            @Path("id") Long id
    );
}
