package com.beispiel.raumreservierung.web;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationUpdateDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String remark,
        String participants

) {}
