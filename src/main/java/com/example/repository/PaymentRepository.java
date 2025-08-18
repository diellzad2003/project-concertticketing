package com.example.repository;

import com.example.entity.Payment;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentRepository extends AbstractRepository<Payment, Integer> {
    public PaymentRepository() { super(Payment.class); }
}
