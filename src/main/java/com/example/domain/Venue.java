package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(
        name = "venues",
        indexes = {
                @Index(name = "idx_venue_name", columnList = "name")
        }
)
@AttributeOverride(name = "id", column = @Column(name = "venue_id"))
public class Venue extends AbstractEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", nullable = false, length = 512)
    private String address;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;


    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Event> events;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Seat> seats;


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}
