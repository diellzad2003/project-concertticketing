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


        boolean success = simulateExternalPaymentGateway(payment);

        if (success) {

            payment.setStatus("SUCCESS");
            payment.setTransactionDate(LocalDateTime.now());
            paymentRepository.create(payment);


            bookingService.finalizeBooking(payment.getBooking());

        } else {

            payment.setStatus("FAILED");
            payment.setTransactionDate(LocalDateTime.now());
            paymentRepository.create(payment);


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

        return (payment.getAmount() != null && payment.getAmount().doubleValue() > 0);
    }
}
