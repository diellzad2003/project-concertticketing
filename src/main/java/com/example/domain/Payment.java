package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "payment_id")),
        @AttributeOverride(name = "createdAt", column = @Column(name = "createdAt", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updatedAt"))

})
public class Payment extends AbstractEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_payments_booking"))
    private Booking booking;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "method", nullable = false, length = 32)
    private String method;

    @Column(name = "status", nullable = false, length = 16) // e.g. SUCCESS/FAILED/REFUNDED
    private String status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;


    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
