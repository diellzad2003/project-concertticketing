package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Payment;
import com.example.repository.PaymentRepository;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.Random;

public class PaymentService extends AbstractService<Payment, Integer> {
    private final Random random = new Random();

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
        return paymentRepository.create(payment);
    }


    public boolean processPayment(Integer bookingId, BigDecimal amount, String method) {
        System.out.println("Processing payment for booking " + bookingId + " amount " + amount);
        return random.nextDouble() < 0.9;
    }
}
