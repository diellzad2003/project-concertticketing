package com.example.service;

import com.example.entity.Booking;
import com.example.entity.Seat;
import com.example.repository.BookingRepository;
import com.example.repository.SeatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class BookingService {

    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private SeatRepository seatRepository;

    /**
     * Reserve seats for a booking request.
     * This method locks (marks PENDING) the seats to prevent double booking during the payment window.
     */
    @Transactional
    public void create(Booking booking) {
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("PENDING");
        bookingRepository.create(booking);
    }
    @Transactional
    public Booking reserveSeats(Integer eventId, List<Seat> seatsToReserve, Integer userId) {
        // 1) check if any of the seats are already locked or sold
        List<Integer> seatIds = seatsToReserve.stream().map(Seat::getSeatId).toList();
        boolean seatsUnavailable = seatRepository.existsLockedOrSold(eventId, seatIds);
        if (seatsUnavailable) {
            throw new IllegalStateException("One or more seats are already taken.");
        }

        // 2) lock (mark PENDING) the seats
        seatRepository.lockSeats(eventId, seatIds);

        // 3) create a booking with status PENDING
        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("PENDING");
        bookingRepository.create(booking);

        return booking;
    }

    /**
     * Call this AFTER successful payment.
     */
    @Transactional
    public void finalizeBooking(Booking booking) {
        // mark seats as SOLD and set booking to CONFIRMED
        seatRepository.markSeatsAsSold(booking);
        booking.setStatus("CONFIRMED");
        bookingRepository.update(booking);
    }

    /**
     * Call this when payment fails or is cancelled by the user.
     */
    @Transactional
    public void releaseBooking(Booking booking) {
        // release seats and set booking to CANCELLED
        seatRepository.releaseSeats(booking);
        booking.setStatus("CANCELLED");
        bookingRepository.update(booking);
    }

    public Booking findById(Integer id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking update(Booking booking) {
        return bookingRepository.update(booking);
    }

    @Transactional
    public void delete(Booking booking) {
        bookingRepository.delete(booking);
    }
}
