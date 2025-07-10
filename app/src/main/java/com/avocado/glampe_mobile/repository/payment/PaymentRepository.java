package com.avocado.glampe_mobile.repository.payment;

import androidx.lifecycle.MutableLiveData;

import com.avocado.glampe_mobile.di.ApiServiceFactory;
import com.avocado.glampe_mobile.model.dto.payment.req.PaymentIntentRequest;
import com.avocado.glampe_mobile.model.dto.payment.resp.PaymentIntentResponse;
import com.avocado.glampe_mobile.model.dto.payment.resp.PaymentResponse;
import com.avocado.glampe_mobile.repository.BaseRepository;
import com.avocado.glampe_mobile.service.PaymentService;

public class PaymentRepository extends BaseRepository {
    private static PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentRepository() {
        this.paymentService = ApiServiceFactory.getInstance().getPaymentService();
    }

    public static PaymentRepository getInstance() {
        if (paymentRepository == null) {
            paymentRepository = new PaymentRepository();
        }
        return paymentRepository;
    }

    public void createIntent(PaymentIntentRequest request,
                             MutableLiveData<PaymentIntentResponse> successLiveData,
                             MutableLiveData<String> errorMessage){
        executeCall(paymentService.createIntent(request), successLiveData, errorMessage);
    }

    public void processCharge(String paymentIntentId,
                              MutableLiveData<PaymentResponse> successLiveData,
                              MutableLiveData<String> errorMessage){
        executeCall(paymentService.processCharge(paymentIntentId), successLiveData, errorMessage);
    }
}
