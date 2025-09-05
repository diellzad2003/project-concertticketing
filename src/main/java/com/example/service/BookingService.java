package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.*;
import com.example.repository.BookingRepository;
import com.example.repository.TicketRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BookingService extends AbstractService<Booking, Integer> {

    @Inject private BookingRepository bookingRepository;
    @Inject private TicketRepository ticketRepository;
    @Inject private PaymentService paymentService;
    @Inject private EntityManager em;

    private static final int RESERVATION_MINUTES = 10;

    @Override
    protected CrudRepository<Booking, Integer> getRepository() {
        return bookingRepository;
    }


    public Booking reserveTickets(Integer userId, Integer eventId, List<Integer> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            throw new IllegalArgumentException("No tickets provided");
        }

        try {
            em.getTransaction().begin();


            List<Ticket> tickets = em.createQuery(
                            "SELECT t FROM Ticket t WHERE t.id IN :ids", Ticket.class)
                    .setParameter("ids", ticketIds)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

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
            User u = new User(); u.setId(userId); booking.setUser(u);
            Event e = new Event(); e.setId(eventId); booking.setEvent(e);

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

            em.persist(booking);
            em.getTransaction().commit();

            return booking;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }


    public Booking reserveSeats(Integer eventId, List<Seat> seats, Integer userId) {
        if (seats == null || seats.isEmpty()) throw new IllegalArgumentException("No seats provided");
        List<Integer> ticketIds = seats.stream().map(Seat::getId).collect(Collectors.toList());
        return reserveTickets(userId, eventId, ticketIds);
    }


    public Booking confirmPayment(Integer bookingId, Payment payment) {
        try {
            em.getTransaction().begin();


            Booking booking = em.find(Booking.class, bookingId, LockModeType.PESSIMISTIC_WRITE);
            if (booking == null) throw new IllegalArgumentException("Booking not found");
            if (booking.getStatus() != BookingStatus.PENDING) {
                throw new IllegalStateException("Booking not pending");
            }


            if (booking.getReservationExpiresAt() != null &&
                    booking.getReservationExpiresAt().isBefore(LocalDateTime.now())) {
                releaseTicketsInternal(booking);
                booking.setStatus(BookingStatus.EXPIRED);
                em.merge(booking);
                em.getTransaction().commit();
                return booking;
            }


            payment.setBooking(booking);
            payment.setAmount(booking.getTotalAmount());

            if (!paymentService.process(payment)) {
                releaseTicketsInternal(booking);
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setPayment(payment);

                em.persist(payment);
                em.merge(booking);
                em.getTransaction().commit();
                return booking;
            }


            for (Ticket t : booking.getTickets()) {
                Ticket locked = em.find(Ticket.class, t.getId(), LockModeType.PESSIMISTIC_WRITE);
                locked.setStatus(TicketStatus.SOLD);
                locked.seteTicketCode(UUID.randomUUID().toString().replace("-", ""));
                em.merge(locked);
            }

            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPayment(payment);
            em.persist(payment);
            em.merge(booking);

            em.getTransaction().commit();
            return booking;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }


    public void cancelBooking(Integer bookingId) {
        try {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, bookingId, LockModeType.PESSIMISTIC_WRITE);
            if (booking != null) {
                if (booking.getStatus() == BookingStatus.CONFIRMED) {
                    throw new IllegalStateException("Cannot cancel a confirmed booking without refund flow");
                }
                releaseTicketsInternal(booking);
                booking.setStatus(BookingStatus.CANCELLED);
                em.merge(booking);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }


    public void expireOldReservations() {
        try {
            em.getTransaction().begin();
            List<Booking> all = em.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();
            for (Booking b : all) {
                if (b.getStatus() == BookingStatus.PENDING &&
                        b.getReservationExpiresAt() != null &&
                        b.getReservationExpiresAt().isBefore(LocalDateTime.now())) {

                    Booking locked = em.find(Booking.class, b.getId(), LockModeType.PESSIMISTIC_WRITE);
                    releaseTicketsInternal(locked);
                    locked.setStatus(BookingStatus.EXPIRED);
                    em.merge(locked);
                }
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }


    private void releaseTicketsInternal(Booking booking) {
        for (Ticket t : booking.getTickets()) {
            Ticket locked = em.find(Ticket.class, t.getId(), LockModeType.PESSIMISTIC_WRITE);
            if (locked.getStatus() == TicketStatus.PENDING) {
                locked.setStatus(TicketStatus.AVAILABLE);
                em.merge(locked);
            }
        }
    }
}
