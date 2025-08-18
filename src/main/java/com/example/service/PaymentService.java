package com.example.service;

import com.example.entity.Payment;
import com.example.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PaymentService {

    @Inject
    private PaymentRepository paymentRepository;

    @Inject
    private BookingService bookingService;

    @Transactional
    public Payment processPayment(Payment payment) {

        // Simulate call to external payment provider
        boolean success = simulateExternalPaymentGateway(payment);

        if (success) {
            // 1) record the payment as SUCCESS
            payment.setStatus("SUCCESS");
            payment.setTransactionDate(LocalDateTime.now());
            paymentRepository.create(payment);

            // 2) finalize booking (confirm and mark seats as SOLD)
            bookingService.finalizeBooking(payment.getBooking());

        } else {
            // 1) mark payment as FAILED
            payment.setStatus("FAILED");
            payment.setTransactionDate(LocalDateTime.now());
            paymentRepository.create(payment);

            // 2) release seats
            bookingService.releaseBooking(payment.getBooking());
        }

        return payment;
    }

    public Payment findById(Integer id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    private boolean simulateExternalPaymentGateway(Payment payment) {
        // Basic simulation
        return (payment.getAmount() != null && payment.getAmount().doubleValue() > 0);
    }
}
