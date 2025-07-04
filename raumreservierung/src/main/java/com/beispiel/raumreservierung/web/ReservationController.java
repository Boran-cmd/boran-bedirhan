package com.beispiel.raumreservierung.web;

import com.beispiel.raumreservierung.model.*;
import com.beispiel.raumreservierung.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/reservation")
@Controller
public class ReservationController {

    private final ReservationRepository reservationRepo;
    private final RoomRepository roomRepo;

    public ReservationController(ReservationRepository reservationRepo, RoomRepository roomRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
    }

    @PostMapping("/reservieren")
    public RedirectView handleReservation(
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam int roomNumber,
            @RequestParam String remark,
            @RequestParam String participants
    ) {
        // Umwandlung
        LocalDate localDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        // Verfügbarkeit prüfen
        boolean conflict = reservationRepo.existsByRoomRoomNumberAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                roomNumber, localDate, end, start
        );
        if (conflict) {
            return new RedirectView("/index.html?error=belegt");
        }

        Room room = roomRepo.findById(roomNumber).orElse(null);
        if (room == null) {
            return new RedirectView("/index.html?error=raum");
        }

        Reservation reservation = new Reservation();
        reservation.setDate(localDate);
        reservation.setStartTime(start);
        reservation.setEndTime(end);
        reservation.setRoom(room);
        reservation.setRemark(remark);
        reservation.setPublicKey(UUID.randomUUID().toString().substring(0, 8));
        reservation.setPrivateKey(UUID.randomUUID().toString().substring(0, 8));

        // Teilnehmer aufteilen
        String[] names = participants.split(",");
        List<Participant> teilnehmer = new ArrayList<>();
        for (String name : names) {
            teilnehmer.add(new Participant(name.trim(), reservation));
        }
        reservation.setParticipants(teilnehmer);

        // Speichern
        reservationRepo.save(reservation);

        // Erfolgreich → Bestätigungsseite
        return new RedirectView("/success.html?private=" + reservation.getPrivateKey() +
                "&public=" + reservation.getPublicKey());

    }
}
