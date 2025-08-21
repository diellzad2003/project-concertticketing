package com.example.service;

import com.example.domain.*;
import com.example.repository.BookingRepository;
import com.example.repository.TicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class BookingService {

    @Inject
    BookingRepository bookingRepository;

    @Inject
    TicketRepository ticketRepository;

    @Inject
    PaymentService paymentService;

    private static final int RESERVATION_MINUTES = 10;


    @Transactional
    public Booking reserveTickets(Integer userId, Integer eventId, List<Integer> ticketIds) {

        List<Ticket> tickets = ticketRepository.findByIdsForUpdate(ticketIds);
        if (tickets.size() != ticketIds.size()) {
            throw new IllegalArgumentException("One or more tickets not found");
        }
        for (Ticket t : tickets) {
            if (!Objects.equals(t.getEvent().getEventId(), eventId)) {
                throw new IllegalStateException("Ticket " + t.getTicketId() + " not part of requested event");
            }
            if (t.getStatus() != TicketStatus.AVAILABLE) {
                throw new IllegalStateException("Ticket " + t.getTicketId() + " is not available");
            }
        }

        tickets.forEach(t -> t.setStatus(TicketStatus.PENDING));


        Booking booking = new Booking();
        User u = new User(); u.setUserId(userId); booking.setUser(u);
        Event e = new Event(); e.setEventId(eventId); booking.setEvent(e);
        booking.setStatus(BookingStatus.PENDING);
        booking.setReservationExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_MINUTES));

        BigDecimal total = BigDecimal.ZERO;
        List<BookingItem> items = new ArrayList<>();
        for (Ticket t : tickets) {
            BookingItem bi = new BookingItem();
            bi.setBooking(booking);
            bi.setTicket(t);
            items.add(bi);
            total = total.add(t.getPrice());
        }
        booking.setItems(items);
        booking.setTotalAmount(total);

        bookingRepository.create(booking);
        return booking;
    }

    @Transactional
    public Booking confirmPayment(Integer bookingId, Payment payment) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId);
        if (booking == null) throw new IllegalArgumentException("Booking not found");
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking not pending");
        }
        if (booking.getReservationExpiresAt() != null && booking.getReservationExpiresAt().isBefore(LocalDateTime.now())) {

            releaseTickets(booking);
            booking.setStatus(BookingStatus.EXPIRED);
            return bookingRepository.update(booking);
        }

        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        if (!paymentService.process(payment)) {

            releaseTickets(booking);
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setPayment(payment);
            return bookingRepository.update(booking);
        }


        for (BookingItem bi : booking.getItems()) {
            Ticket t = bi.getTicket();
            Ticket locked = ticketRepository.findByIdForUpdate(t.getTicketId());
            locked.setStatus(TicketStatus.SOLD);
            locked.setEticketCode(UUID.randomUUID().toString().replaceAll("-", ""));
            ticketRepository.update(locked);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPayment(payment);
        return bookingRepository.update(booking);
    }


    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId);
        if (booking == null) return;
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel a confirmed booking without refund flow");
        }
        releaseTickets(booking);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.update(booking);
    }


    @Transactional
    public void expireOldReservations() {
        List<Booking> all = bookingRepository.findAll();
        for (Booking b : all) {
            if (b.getStatus() == BookingStatus.PENDING
                    && b.getReservationExpiresAt() != null
                    && b.getReservationExpiresAt().isBefore(LocalDateTime.now())) {
                Booking locked = bookingRepository.findByIdForUpdate(b.getBookingId());
                releaseTickets(locked);
                locked.setStatus(BookingStatus.EXPIRED);
                bookingRepository.update(locked);
            }
        }
    }


    private void releaseTickets(Booking booking) {
        for (BookingItem bi : booking.getItems()) {
            Ticket t = ticketRepository.findByIdForUpdate(bi.getTicket().getTicketId());
            if (t.getStatus() == TicketStatus.PENDING) {
                t.setStatus(TicketStatus.AVAILABLE);
                ticketRepository.update(t);
            }
        }
    }
}
