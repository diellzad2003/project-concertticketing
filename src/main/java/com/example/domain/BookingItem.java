package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "booking_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_booking_items_ticket", columnNames = "ticket_id")
        }
)
@AttributeOverride(name = "id", column = @Column(name = "item_id"))
public class BookingItem extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_booking_items_booking"))
    private Booking booking;

    @OneToOne(optional = false)
    @JoinColumn(name = "ticket_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_booking_items_ticket"))
    private Ticket ticket;

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}
