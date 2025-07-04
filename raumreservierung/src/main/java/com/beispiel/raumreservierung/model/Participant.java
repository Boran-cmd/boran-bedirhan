package com.beispiel.raumreservierung.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Participant() {
    }

    public Participant(String name, Reservation reservation) {
        this.name = name;
        this.reservation = reservation;
    }

    // Getter + Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}
