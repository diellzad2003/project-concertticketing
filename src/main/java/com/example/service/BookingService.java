package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.*;
import com.example.repository.BookingRepository;
import com.example.repository.TicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@ApplicationScoped
public class BookingService extends AbstractService<Booking, Integer> {

    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private PaymentService paymentService;

    private static final int RESERVATION_MINUTES = 10;

    @Override
    protected CrudRepository<Booking, Integer> getRepository() {
        return bookingRepository;
    }


    @Transactional
    public Booking reserveTickets(Integer userId, Integer eventId, List<Integer> ticketIds) {

        List<Ticket> tickets = ticketRepository.findByIdsForUpdate(ticketIds);
        if (tickets.size() != ticketIds.size()) {
            throw new IllegalArgumentException("One or more tickets not found");
        }

        for (Ticket t : tickets) {
            if (!Objects.equals(t.getEvent().getId(), eventId)) {
                throw new IllegalStateException("Ticket " + t.getId() + " not part of requested event");
            }
            if (t.getStatus() != TicketStatus.AVAILABLE) {
                throw new IllegalStateException("Ticket " + t.getId() + " is not available");
            }
        }


        tickets.forEach(t -> t.setStatus(TicketStatus.PENDING));

        Booking booking = new Booking();

        User u = new User();
        u.setId(userId);
        booking.setUser(u);

        Event e = new Event();
        e.setId(eventId);
        booking.setEvent(e);

        booking.setStatus(BookingStatus.PENDING);
        booking.setReservationExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_MINUTES));


        BigDecimal total = tickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        booking.setTotalAmount(total);


        List<BookingItem> items = new ArrayList<>();
        for (Ticket t : tickets) {
            BookingItem bi = new BookingItem();
            bi.setBooking(booking);
            bi.setTicket(t);
            items.add(bi);


            t.setBooking(booking);
        }

        booking.setItems(items);
        booking.setTickets(tickets);

        bookingRepository.create(booking);
        return booking;
    }


    @Transactional
    public Booking reserveSeats(Integer eventId, List<Seat> seats, Integer userId) {
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("No seats provided");
        }
        List<Integer> ticketIds = seats.stream().map(Seat::getId).toList();
        return reserveTickets(userId, eventId, ticketIds);
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


        for (Ticket t : booking.getTickets()) {
            Ticket locked = ticketRepository.findByIdForUpdate(t.getId());
            locked.setStatus(TicketStatus.SOLD);
            locked.seteTicketCode(UUID.randomUUID().toString().replaceAll("-", ""));
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
                Booking locked = bookingRepository.findByIdForUpdate(b.getId());
                releaseTickets(locked);
                locked.setStatus(BookingStatus.EXPIRED);
                bookingRepository.update(locked);
            }
        }
    }


    private void releaseTickets(Booking booking) {
        for (Ticket t : booking.getTickets()) {
            Ticket locked = ticketRepository.findByIdForUpdate(t.getId());
            if (locked.getStatus() == TicketStatus.PENDING) {
                locked.setStatus(TicketStatus.AVAILABLE);
                ticketRepository.update(locked);
            }
        }
    }
}
