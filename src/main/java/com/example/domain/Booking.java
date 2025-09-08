package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_bookings_user",  columnList = "user_id"),
                @Index(name = "idx_bookings_event", columnList = "event_id")
        }
)
@AttributeOverride(name = "id", column = @Column(name = "booking_id"))
public class Booking extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    @Column(name = "reservation_expires_at")
    private LocalDateTime reservationExpiresAt;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, optional = true)
    private Payment payment;


    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public List<BookingItem> getItems() { return items; }
    public void setItems(List<BookingItem> items) { this.items = items; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public LocalDateTime getReservationExpiresAt() { return reservationExpiresAt; }
    public void setReservationExpiresAt(LocalDateTime reservationExpiresAt) { this.reservationExpiresAt = reservationExpiresAt; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}
