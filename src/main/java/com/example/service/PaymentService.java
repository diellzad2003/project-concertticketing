package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.Booking;
import com.example.domain.Payment;
import com.example.repository.PaymentRepository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

public class PaymentService extends AbstractService<Payment, Integer> {

    private final Random random = new Random();

    @Inject private PaymentRepository paymentRepository;
    @Inject private EntityManager entityManager;

    @Override
    protected CrudRepository<Payment, Integer> getRepository() {
        return paymentRepository;
    }

    public boolean process(Payment payment) {
        return payment != null
                && payment.getAmount() != null
                && payment.getAmount().compareTo(BigDecimal.ZERO) > 0
                && payment.getMethod() != null
                && payment.getBooking() != null
                && payment.getBooking().getId() != null;
    }

    private Payment findByBookingId(Integer bookingId) {
        return entityManager.createQuery("""
            SELECT p FROM Payment p
            WHERE p.booking.id = :bid
        """, Payment.class)
                .setParameter("bid", bookingId)
                .getResultStream().findFirst().orElse(null);
    }

    @Transactional
    public Payment processPayment(Payment payment) {
        if (!process(payment)) {
            throw new IllegalArgumentException("Invalid payment payload (booking.id, amount, method are required)");
        }


        Integer bookingId = payment.getBooking().getId();
        Booking managedBooking = entityManager.getReference(Booking.class, bookingId);
        payment.setBooking(managedBooking);


        Payment existing = findByBookingId(bookingId);
        if (existing != null) {
            throw new WebApplicationException("A payment already exists for this booking.", Response.Status.CONFLICT);
        }


        if (payment.getTransactionDate() == null) {
            payment.setTransactionDate(LocalDateTime.now());
        }
        if (payment.getStatus() == null) {
            payment.setStatus("SUCCESS");
        }
        if (!"SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalArgumentException("Payment failed or invalid.");
        }

        return paymentRepository.create(payment);
    }


    public boolean processPayment(Integer bookingId, BigDecimal amount, String method) {
        System.out.println("Processing payment for booking " + bookingId + " amount " + amount);
        return random.nextDouble() < 0.9;
    }


    @Override
    @Transactional
    public Payment update(Payment incoming) {
        if (incoming == null || incoming.getId() == null) {
            throw new IllegalArgumentException("Payment id is required for update.");
        }

        Payment managed = entityManager.find(Payment.class, incoming.getId());
        if (managed == null) {
            throw new WebApplicationException("Payment not found.", Response.Status.NOT_FOUND);
        }


        if (incoming.getBooking() != null && incoming.getBooking().getId() != null
                && !managed.getBooking().getId().equals(incoming.getBooking().getId())) {
            throw new WebApplicationException("Changing the booking of a payment is not allowed.", Response.Status.BAD_REQUEST);
        }


        if (incoming.getStatus() != null) {
            managed.setStatus(incoming.getStatus());
        }
        if (incoming.getTransactionDate() != null) {
            managed.setTransactionDate(incoming.getTransactionDate());
        }

        return paymentRepository.update(managed);
    }
}
