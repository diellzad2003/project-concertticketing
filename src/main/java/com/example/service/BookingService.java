package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.*;
import com.example.repository.BookingItemRepository;
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
    @Inject private BookingItemRepository bookingItemRepository;
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
                            "SELECT t FROM Ticket t " +
                                    "WHERE t.id IN :ids AND t.event.id = :eid AND t.status = :st",
                            Ticket.class)
                    .setParameter("ids", ticketIds)
                    .setParameter("eid", eventId)
                    .setParameter("st", TicketStatus.AVAILABLE)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

            if (tickets.size() != ticketIds.size()) {
                throw new IllegalStateException("One or more tickets are not available or not in this event");
            }


            for (Ticket t : tickets) t.setStatus(TicketStatus.HELD);

            User userRef = em.getReference(User.class, userId);
            Event eventRef = em.getReference(Event.class, eventId);

            Booking booking = new Booking();
            booking.setUser(userRef);
            booking.setEvent(eventRef);
            booking.setStatus(BookingStatus.PENDING);
            booking.setBookingTime(LocalDateTime.now());
            booking.setReservationExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_MINUTES));

            BigDecimal total = tickets.stream()
                    .map(Ticket::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            booking.setTotalAmount(total);


            em.persist(booking);


            List<BookingItem> items = new ArrayList<>();
            for (Ticket t : tickets) {
                t.setBooking(booking);
                em.merge(t);

                BookingItem bi = new BookingItem();
                bi.setBooking(booking);
                bi.setTicket(t);
                bookingItemRepository.create(bi);
                items.add(bi);
            }

            try { booking.setItems(items); } catch (Exception ignore) {}
            try { booking.setTickets(tickets); } catch (Exception ignore) {}

            em.getTransaction().commit();
            return booking;

        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        }
    }


    public Booking reserveSeats(Integer eventId, List<Seat> seats, Integer userId) {
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("No seats provided");
        }
        List<Integer> seatIds = seats.stream().map(Seat::getId).collect(Collectors.toList());

        List<Integer> ticketIds = em.createQuery(
                        "SELECT t.id FROM Ticket t WHERE t.event.id = :eid AND t.seat.id IN :sid",
                        Integer.class)
                .setParameter("eid", eventId)
                .setParameter("sid", seatIds)
                .getResultList();

        if (ticketIds.size() != seatIds.size()) {
            throw new IllegalStateException("Some seats have no ticket for this event or are duplicates");
        }

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
            payment.setTransactionDate(LocalDateTime.now());


            boolean ok = paymentService.process(payment);

            if (!ok) {
                payment.setStatus("FAILED");
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
            payment.setStatus("SUCCESS");
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
            if (locked.getStatus() == TicketStatus.HELD) {      // revert HELD, not PENDING
                locked.setStatus(TicketStatus.AVAILABLE);
                em.merge(locked);
            }
        }
    }
    public Booking purchase(Integer userId,
                            Integer eventId,
                            List<Integer> ticketIds,
                            String method) {


        Booking booking = reserveTickets(userId, eventId, ticketIds);


        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setMethod(method);
        payment.setStatus("PENDING");
        payment.setTransactionDate(LocalDateTime.now());


        try {
            return confirmPayment(booking.getId(), payment);
        } catch (RuntimeException e) {
            try { cancelBooking(booking.getId()); } catch (Exception ignore) {}
            throw e;
        }
    }

}
