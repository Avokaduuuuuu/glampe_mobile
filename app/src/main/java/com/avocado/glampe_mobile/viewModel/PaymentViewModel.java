package com.avocado.glampe_mobile.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avocado.glampe_mobile.model.dto.payment.req.PaymentIntentRequest;
import com.avocado.glampe_mobile.model.dto.payment.resp.PaymentIntentResponse;
import com.avocado.glampe_mobile.model.dto.payment.resp.PaymentResponse;
import com.avocado.glampe_mobile.repository.payment.PaymentRepository;

import lombok.Getter;


@Getter
public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<PaymentIntentResponse> paymentIntent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createIntentLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> createIntentError = new MutableLiveData<>();

    private final MutableLiveData<PaymentResponse> paymentResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> processChargeLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> processChargeError = new MutableLiveData<>();

    private PaymentRepository paymentRepository;

    public PaymentViewModel() {
        this.paymentRepository = PaymentRepository.getInstance();
    }

    public void createIntent(PaymentIntentRequest request) {
        createIntentLoading.setValue(true);

        paymentRepository.createIntent(request, paymentIntent, createIntentError);
    }

    public void processCharge(String paymentIntentId) {
        processChargeLoading.setValue(true);

        paymentRepository.processCharge(paymentIntentId, paymentResponse, processChargeError);
    }
}
