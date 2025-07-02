package com.avocado.glampe_mobile.service;

import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.PageResponse;
import com.avocado.glampe_mobile.model.dto.booking.req.BookingRequest;
import com.avocado.glampe_mobile.model.dto.booking.resp.BookingResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface BookingService {
    @GET("/api/v1/bookings")
    Call<ApiResponse<PageResponse<BookingResponse>>> fetchBookings(
            @QueryMap Map<String, String> params
            );

    @POST("/api/v1/bookings")
    Call<ApiResponse<BookingResponse>> addBooking(
            @Body BookingRequest request
            );

    @GET("/api/v1/bookings/{id}")
    Call<ApiResponse<BookingResponse>> fetchBookingById(@Path("id") Long id);
}
