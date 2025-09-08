package com.example.domain;

import com.example.common.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_venue_row_num",
                        columnNames = {"venue_id", "row_label", "number"}
                )
        }
)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "seat_id"))

})
public class Seat extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_seat_venue"))
    private Venue venue;

    @Column(name = "section", length = 64)
    private String section;

    @Column(name = "row_label", nullable = false, length = 16)
    private String rowLabel;

    @Column(name = "number", nullable = false)
    private Integer number;

    // getters and setters
    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
}
