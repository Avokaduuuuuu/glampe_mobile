package com.avocado.glampe_mobile.service;


import com.avocado.glampe_mobile.model.dto.ApiResponse;
import com.avocado.glampe_mobile.model.dto.payment.req.PaymentIntentRequest;
import com.avocado.glampe_mobile.model.dto.payment.resp.PaymentIntentResponse;
import com.avocado.glampe_mobile.model.dto.payment.resp.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentService {
    @POST("/api/v1/payments/create-intent")
    Call<ApiResponse<PaymentIntentResponse>> createIntent(@Body PaymentIntentRequest request);

    @POST("/api/v1/payments/process-charge")
    Call<ApiResponse<PaymentResponse>> processCharge(@Query("paymentIntentId") String paymentIntentId);
}
