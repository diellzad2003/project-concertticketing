package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "tickets",
        uniqueConstraints = {

                @UniqueConstraint(name = "uk_ticket_event_seat", columnNames = {"event_id", "seat_id"})
        },
        indexes = {
                @Index(name = "idx_ticket_event", columnList = "event_id"),
                @Index(name = "idx_ticket_seat", columnList = "seat_id"),
                @Index(name = "idx_ticket_booking", columnList = "booking_id"),
                @Index(name = "idx_ticket_eticket", columnList = "eticket_code")
        }
)
@AttributeOverride(name = "id", column = @Column(name = "ticket_id"))
public class Ticket extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ticket_event"))
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ticket_seat"))
    private Seat seat;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private TicketStatus status = TicketStatus.AVAILABLE;

    @Column(name = "eticket_code", length = 64, unique = true)
    private String eTicketCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id",
            foreignKey = @ForeignKey(name = "fk_ticket_booking"))
    private Booking booking;


    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public String geteTicketCode() { return eTicketCode; }
    public void seteTicketCode(String eTicketCode) { this.eTicketCode = eTicketCode; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
