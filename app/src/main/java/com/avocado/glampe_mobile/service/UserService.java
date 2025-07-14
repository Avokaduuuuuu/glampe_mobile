package com.avocado.glampe_mobile.service;

import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.fcmtoken.req.FcmTokenUpdateRequest;
import com.avocado.glampe_mobile.model.dto.fcmtoken.resp.FcmTokenResponse;
import com.avocado.glampe_mobile.model.dto.user.req.UserCreateRequest;
import com.avocado.glampe_mobile.model.dto.user.req.UserVerifyRequest;
import com.avocado.glampe_mobile.model.dto.user.resp.AuthUserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @POST("/api/v1/users/verify")
    Call<ApiResponse<AuthUserResponse>> verifyUser(
            @Body UserVerifyRequest request
            );

    @POST("/api/v1/users")
    Call<ApiResponse<AuthUserResponse>> addUser(
            @Body UserCreateRequest request
            );

    @POST("/api/v1/users/fcm-tokens")
    Call<ApiResponse<FcmTokenResponse>> saveFcmToken(
            @Body FcmTokenUpdateRequest request
            );

    @DELETE("/api/v1/users/fcm-tokens")
    Call<ApiResponse<String>> deleteToken(
            @Query("userId") Long userId,
            @Query("deviceId") String deviceId
    );
}

