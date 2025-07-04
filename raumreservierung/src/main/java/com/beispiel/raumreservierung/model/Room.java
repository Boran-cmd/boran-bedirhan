package com.beispiel.raumreservierung.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Room {

    @Id
    private int roomNumber; // z. B. 101

    private String name; // z. B. „Sitzungszimmer 101“

    public Room() {
    }

    public Room(int roomNumber, String name) {
        this.roomNumber = roomNumber;
        this.name = name;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
