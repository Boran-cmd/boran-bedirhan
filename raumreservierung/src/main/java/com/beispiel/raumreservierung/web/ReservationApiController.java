package com.beispiel.raumreservierung.web;

import com.beispiel.raumreservierung.model.*;
import com.beispiel.raumreservierung.repository.*;
import com.beispiel.raumreservierung.web.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/reservation")
public class ReservationApiController {

    private final ReservationRepository reservationRepo;
    private final RoomRepository roomRepo;

    public ReservationApiController(ReservationRepository reservationRepo, RoomRepository roomRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
    }

    // Neue Reservierung
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationCreateDto dto) {
        // Verfügbarkeitsprüfung
        boolean belegt = reservationRepo.existsByRoomRoomNumberAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                Integer.parseInt(dto.roomName()), dto.date(), dto.endTime(), dto.startTime());
        if (belegt) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("❌ Der Raum ist in diesem Zeitraum bereits belegt.");
        }

        Room room = roomRepo.findById(Integer.parseInt(dto.roomName()))
                .orElseThrow(() -> new NoSuchElementException("Raum nicht gefunden"));


        Reservation reservation = new Reservation();
        reservation.setDate(dto.date());
        reservation.setStartTime(dto.startTime());
        reservation.setEndTime(dto.endTime());
        reservation.setRemark(dto.remark());
        reservation.setRoom(room);
        reservation.setPrivateKey(UUID.randomUUID().toString());
        reservation.setPublicKey(UUID.randomUUID().toString());

        List<Participant> participants = Arrays.stream(dto.participants().split(","))
                .map(name -> new Participant(name.trim(), reservation))
                .collect(Collectors.toList());

        reservation.setParticipants(participants);
        reservationRepo.save(reservation);

        Map<String, String> keys = new HashMap<>();
        keys.put("privateKey", reservation.getPrivateKey());
        keys.put("publicKey", reservation.getPublicKey());

        return ResponseEntity.ok(keys);
    }

    @PutMapping("/private/{key}")
    public void updateReservation(@PathVariable String key, @RequestBody ReservationUpdateDto dto) {
        Reservation res = reservationRepo.findByPrivateKey(key).orElseThrow();
        res.setDate(dto.date());
        res.setStartTime(dto.startTime());
        res.setEndTime(dto.endTime());
        res.setRemark(dto.remark());

        // Hole die bestehende Liste
        List<Participant> participants = res.getParticipants();
        participants.clear(); // Leere die Liste (dadurch werden alte Teilnehmer als "Waisen" erkannt und gelöscht)

// Füge neue Teilnehmer hinzu
        for (String name : dto.participants().split(",")) {
            participants.add(new Participant(name.trim(), res));
        }

// KEIN setParticipants() mehr aufrufen!
        reservationRepo.save(res);

    }

    @DeleteMapping("/private/{key}")
    public void deleteReservation(@PathVariable String key) {
        reservationRepo.findByPrivateKey(key).ifPresent(reservationRepo::delete);
    }

    @GetMapping("/public/{key}")
    public Reservation getByPublicKey(@PathVariable String key) {
        return reservationRepo.findByPublicKey(key).orElseThrow();
    }

    @GetMapping("/private/{key}")
    public ResponseEntity<Reservation> getByPrivateKey(@PathVariable String key) {
        return reservationRepo.findByPrivateKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/all")
    public List<Reservation> getAllReservations() {
        return reservationRepo.findAll();
    }

    @GetMapping("/admin/by-date")
    public List<Reservation> getByDate(@RequestParam("date") LocalDate date) {
        return reservationRepo.findByDate(date);
    }
}
