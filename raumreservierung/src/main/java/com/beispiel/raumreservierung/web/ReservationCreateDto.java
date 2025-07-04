package com.beispiel.raumreservierung.web;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationCreateDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String roomName,
        String remark,
        String participants
) {}
