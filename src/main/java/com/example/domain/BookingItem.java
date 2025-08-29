package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_items")
public class BookingItem extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;



    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
