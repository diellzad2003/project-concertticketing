package com.example.service;

import com.example.domain.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class PaymentService {


    public boolean process(Payment payment) {
        return payment != null
                && payment.getAmount() != null
                && payment.getAmount().compareTo(BigDecimal.ZERO) > 0
                && payment.getMethod() != null;
    }
}
