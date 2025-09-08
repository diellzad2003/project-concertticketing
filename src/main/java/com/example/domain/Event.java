package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "events",
        indexes = {
                @Index(name = "ix_events_venue",      columnList = "venue_id"),
                @Index(name = "ix_events_start_end",  columnList = "start_datetime,end_datetime"),
                @Index(name = "ix_events_organizer",  columnList = "organizer_id")   // NEW
        }
)
@AttributeOverrides({
        @AttributeOverride(name = "id",        column = @Column(name = "event_id")),
        @AttributeOverride(name = "createdAt", column = @Column(name = "createdAt", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updatedAt"))
})
public class Event extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_events_venue"))
    private Venue venue;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_events_organizer"))
    private User organizer;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;


    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }

    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartDatetime() { return startDatetime; }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime = startDatetime; }

    public LocalDateTime getEndDatetime() { return endDatetime; }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime = endDatetime; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}
