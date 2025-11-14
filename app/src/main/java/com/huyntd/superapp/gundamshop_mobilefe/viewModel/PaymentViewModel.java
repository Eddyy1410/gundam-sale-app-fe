package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.request.PaymentRequest;
import com.huyntd.superapp.gundamshop_mobilefe.repository.PaymentRepository;

public class PaymentViewModel extends ViewModel {
    private final PaymentRepository repository = PaymentRepository.getInstance();;

    public LiveData<String> createVNPAYPayment(PaymentRequest request) {
        return repository.createVNPAYPayment(request);
    }

    public LiveData<String> createMomoPayment(PaymentRequest request) {
        return repository.createMomoPayment(request);
    }
}

