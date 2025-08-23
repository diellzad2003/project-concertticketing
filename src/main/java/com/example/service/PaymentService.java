package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Payment;
import com.example.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@ApplicationScoped
public class PaymentService extends AbstractService<Payment, Integer> {

    @Inject
    private PaymentRepository paymentRepository;

    @Override
    protected CrudRepository<Payment, Integer> getRepository() {
        return paymentRepository;
    }


    public boolean process(Payment payment) {
        return payment != null
                && payment.getAmount() != null
                && payment.getAmount().compareTo(BigDecimal.ZERO) > 0
                && payment.getMethod() != null;
    }
    public Payment processPayment(Payment payment) {
        if (!process(payment)) {
            payment.setStatus("FAILED");
            return payment;
        }

        payment.setStatus("SUCCESS");
        return getRepository().create(payment);
    }


}
