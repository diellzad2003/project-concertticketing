package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "venues")
public class Venue extends AbstractEntity {

    private String name;

    private String address;

    private Integer capacity;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Event> events;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Seat> seats;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
