package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    private String section;

    private String rowLabel;

    private Integer number;



    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
